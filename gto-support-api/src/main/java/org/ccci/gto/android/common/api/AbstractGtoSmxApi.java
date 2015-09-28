package org.ccci.gto.android.common.api;

import static java.net.HttpURLConnection.HTTP_OK;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.ccci.gto.android.common.api.AbstractApi.Request.MediaType;
import org.ccci.gto.android.common.api.AbstractGtoSmxApi.Request;
import org.ccci.gto.android.common.api.AbstractTheKeyApi.ExecutionContext;
import org.ccci.gto.android.common.api.AbstractTheKeyApi.Session;
import org.ccci.gto.android.common.util.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import me.thekey.android.TheKey;
import me.thekey.android.TheKeySocketException;

public abstract class AbstractGtoSmxApi
        extends AbstractTheKeyApi<Request, ExecutionContext<Session>, Session> {
    private static final String PARAM_APPVERSION = "_appVersion";

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

    protected AbstractGtoSmxApi(@NonNull final Context context, @NonNull final TheKey thekey,
                                @NonNull final Uri apiUri, @NonNull final String prefFile,
                                @Nullable final String guid) {
        super(context, thekey, apiUri, prefFile, guid);

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
        assert request.context != null;
        return new Session(prefs, request.context.guid);
    }

    @Nullable
    @Override
    protected Session establishSession(@NonNull final Request request) throws ApiException {
        assert request.context != null;

        // short-circuit if we don't have a guid to establish a session for
        if (request.context.guid == null) {
            // can't establish a session for an invalid user
            return null;
        }

        // establish a session
        final String sessionId;
        if ("GUEST".equals(request.context.guid) && this.allowGuest) {
            sessionId = this.guestLogin();
        } else {
            // short-circuit if we don't have a valid service
            final String service = getService();
            if (service == null) {
                return null;
            }

            // get a ticket for the specified service
            final String ticket;
            try {
                ticket = mTheKey.getTicket(request.context.guid, service);
            } catch (final TheKeySocketException e) {
                throw new ApiSocketException(e);
            }

            // short-circuit if we don't have a valid ticket
            if (ticket == null) {
                return null;
            }

            // login to the hub
            sessionId = this.login(ticket);
        }

        // create & return a session object
        return sessionId != null ? new Session(sessionId, request.context.guid) : null;
    }

    @Override
    protected void onPrepareUri(@NonNull final Uri.Builder uri, @NonNull final Request request)
            throws ApiException {
        assert request.context != null;

        // prepend api method url with the session id
        if (request.useSession && request.context.session != null) {
            uri.appendPath(request.context.session.id);
        }

        // generate uri
        super.onPrepareUri(uri, request);

        // append the app version to the uri
        if (this.includeAppVersion) {
            uri.appendQueryParameter(PARAM_APPVERSION, this.appVersion);
        }
    }

    @Nullable
    @Override
    protected String getDefaultService() {
        return mBaseUri.buildUpon().appendEncodedPath("auth/login").toString();
    }

    @Nullable
    protected String getService() throws ApiException {
        // short-circuit if we have a cached service
        String service = super.getCachedService();
        if (service != null) {
            return service;
        }

        // try loading the service directly from the API
        service = this.getServiceFromApi();

        // try the default service again as a last resort
        if (service == null) {
            service = this.getDefaultService();
        }

        // we found a service, let's store it for future use before returning
        if (service != null) {
            this.setCachedService(service);
        }
        return service;
    }

    @Nullable
    @WorkerThread
    protected String getServiceFromApi() throws ApiException {
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

    @Nullable
    @WorkerThread
    protected String login(@Nullable final String ticket) throws ApiException {
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

    @Nullable
    @WorkerThread
    protected String guestLogin() throws ApiException {
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

    /**
     * class that represents a request being sent to the api
     */
    protected static class Request extends AbstractTheKeyApi.Request<ExecutionContext<Session>, Session> {
        public Request(@NonNull final String path) {
            super(path);
        }
    }
}
