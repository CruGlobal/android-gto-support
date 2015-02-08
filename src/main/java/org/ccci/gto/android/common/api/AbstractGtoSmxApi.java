package org.ccci.gto.android.common.api;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.api.AbstractApi.Request.MediaType;
import org.ccci.gto.android.common.api.AbstractGtoSmxApi.Request;
import org.ccci.gto.android.common.api.AbstractTheKeyApi.Session;
import org.ccci.gto.android.common.util.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.thekey.android.TheKey;
import me.thekey.android.TheKeySocketException;

public abstract class AbstractGtoSmxApi extends AbstractTheKeyApi<Request, Session> {
    private static final String PARAM_APPVERSION = "_appVersion";

    private final Executor asyncExecutor;

    private final String appVersion;
    private boolean includeAppVersion = false;
    private boolean allowGuest = false;

    protected AbstractGtoSmxApi(@NonNull final Context context, @NonNull final TheKey thekey,
                                @NonNull final String apiUri, @NonNull final String prefFile) {
        this(context, thekey, apiUri, prefFile, null);
    }

    protected AbstractGtoSmxApi(@NonNull final Context context, @NonNull final TheKey thekey,
                                @NonNull final String apiUri, @NonNull final String prefFile,
                                @Nullable final String guid) {
        this(context, thekey, Uri.parse(apiUri.endsWith("/") ? apiUri : apiUri + "/"), prefFile, guid);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected AbstractGtoSmxApi(@NonNull final Context context, @NonNull final TheKey thekey,
                                @NonNull final Uri apiUri, @NonNull final String prefFile,
                                @Nullable final String guid) {
        super(context, thekey, apiUri, prefFile, guid);
        this.asyncExecutor = Executors.newFixedThreadPool(1);
        if (this.asyncExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) this.asyncExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                ((ThreadPoolExecutor) this.asyncExecutor).allowCoreThreadTimeOut(true);
            } else {
                ((ThreadPoolExecutor) this.asyncExecutor).setCorePoolSize(0);
            }
        }

        // generate the app version string
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append(context.getPackageName());
            sb.append("/");
            sb.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (final Exception e) {
            // this isn't critical so suppress all exceptions
        }
        this.appVersion = sb.toString();
    }

    public void setIncludeAppVersion(final boolean includeAppVersion) {
        this.includeAppVersion = includeAppVersion;
    }

    public void setAllowGuest(final boolean allowGuest) {
        this.allowGuest = allowGuest;
    }

    @Nullable
    @Override
    protected String getActiveGuid() {
        String guid = super.getActiveGuid();

        if (guid == null && this.allowGuest) {
            guid = "GUEST";
        }

        return guid;
    }

    @Nullable
    @Override
    protected final Session loadSession(@NonNull final SharedPreferences prefs, @NonNull final Request request) {
        return new Session(prefs, request.guid);
    }

    @Nullable
    @Override
    protected Session establishSession(@NonNull final Request request) throws ApiException {
        // short-circuit if we don't have a guid to establish a session for
        if (request.guid == null) {
            // can't establish a session for an invalid user
            return null;
        }

        // establish a session
        final String sessionId;
        if ("GUEST".equals(request.guid) && this.allowGuest) {
            sessionId = this.guestLogin();
        } else {
            // get the service to retrieve a ticket for
            final String service = this.getService();

            // get a ticket for the specified service
            final TheKey.TicketAttributesPair ticket;
            try {
                ticket = mTheKey.getTicketAndAttributes(service);
            } catch (final TheKeySocketException e) {
                throw new ApiSocketException(e);
            }

            // short-circuit if we don't have a valid ticket
            if (ticket == null || !request.guid.equals(ticket.attributes.getGuid())) {
                return null;
            }

            // login to the hub
            sessionId = this.login(ticket.ticket);
        }

        // create & return a session object
        return sessionId != null ? new Session(sessionId, request.guid) : null;
    }

    @Override
    protected void onPrepareUri(@NonNull final Uri.Builder uri, @NonNull final Request request)
            throws ApiException {
        // prepend api method url with the session id
        if (request.useSession && request.session != null) {
            uri.appendPath(request.session.id);
        }

        // generate uri
        super.onPrepareUri(uri, request);

        // append the app version to the uri
        if (this.includeAppVersion) {
            uri.appendQueryParameter(PARAM_APPVERSION, this.appVersion);
        }
    }

    @Override
    protected boolean isSessionInvalid(@NonNull final HttpURLConnection conn, @NonNull final Request request)
            throws IOException {
        // short-circuit if we already know the session is invalid
        if (super.isSessionInvalid(conn, request)) {
            return true;
        }

        // Check 401 Unauthorized responses to see if it's because of needing CAS authentication
        if (conn.getResponseCode() == HTTP_UNAUTHORIZED) {
            String auth = conn.getHeaderField("WWW-Authenticate");
            if (auth != null) {
                auth = auth.trim();
                int i = auth.indexOf(" ");
                if (i != -1) {
                    auth = auth.substring(0, i);
                }
                auth = auth.toUpperCase(Locale.US);
            } else {
                // there isn't an auth header, so assume it is the CAS scheme
                auth = "CAS";
            }

            // the 401 is requesting CAS auth, assume session is invalid
            return "CAS".equals(auth);
        }

        return false;
    }

    public String getService() throws ApiException {
        final Request request = new Request("auth/service");
        request.useSession = false;
        request.accept = MediaType.TEXT_PLAIN;
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            if (conn.getResponseCode() == HTTP_OK) {
                return IOUtils.readString(conn.getInputStream());
            }
        } catch (final InvalidSessionApiException e) {
            throw new RuntimeException("unexpected exception", e);
        } catch (final IOException e) {
            throw new ApiSocketException(e);
        } finally {
            IOUtils.closeQuietly(conn);
        }

        return null;
    }

    public String login(final String ticket) throws ApiException {
        // don't attempt to login if we don't have a ticket
        if (ticket == null) {
            return null;
        }

        // build request
        final Request request = new Request("auth/login");
        request.useSession = false;
        request.method = AbstractApi.Request.Method.POST;
        request.accept = AbstractApi.Request.MediaType.TEXT_PLAIN;
        try {
            request.setContent(MediaType.APPLICATION_FORM_URLENCODED,
                               ("ticket=" + URLEncoder.encode(ticket, "UTF-8")).getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("unexpected error, UTF-8 encoding doesn't exist?", e);
        }

        // issue login request
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            // was this a valid login
            if (conn.getResponseCode() == HTTP_OK) {
                // the sessionId is returned as the body of the response
                return IOUtils.readString(conn.getInputStream());
            }
        } catch (final InvalidSessionApiException ignored) {
        } catch (final MalformedURLException e) {
            throw new RuntimeException("unexpected exception", e);
        } catch (final IOException e) {
            throw new ApiSocketException(e);
        } finally {
            IOUtils.closeQuietly(conn);
        }

        return null;
    }

    public String guestLogin() throws ApiException {
        // build request
        final Request request = new Request("auth/login");
        request.useSession = false;
        request.method = AbstractApi.Request.Method.POST;
        request.accept = MediaType.TEXT_PLAIN;
        request.setContent(MediaType.APPLICATION_FORM_URLENCODED, "guest=true");

        // issue login request
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            // was this a valid login
            if (conn.getResponseCode() == HTTP_OK) {
                // the sessionId is returned as the body of the response
                return IOUtils.readString(conn.getInputStream());
            }
        } catch (final InvalidSessionApiException ignored) {
        } catch (final MalformedURLException e) {
            throw new RuntimeException("unexpected exception", e);
        } catch (final IOException e) {
            throw new ApiSocketException(e);
        } finally {
            IOUtils.closeQuietly(conn);
        }

        return null;
    }

    public final void async(final Runnable task) {
        this.asyncExecutor.execute(task);
    }

    /**
     * class that represents a request being sent to the api
     */
    protected static class Request extends AbstractTheKeyApi.Request<Session> {
        public Request(@NonNull final String path) {
            super(path);
        }
    }
}
