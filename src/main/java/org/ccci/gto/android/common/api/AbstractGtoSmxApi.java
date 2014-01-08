package org.ccci.gto.android.common.api;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;

import org.ccci.gto.android.common.util.IOUtils;
import org.ccci.gto.android.common.util.UriUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.thekey.android.TheKey;
import me.thekey.android.TheKeySocketException;

public abstract class AbstractGtoSmxApi {
    private static final String PREF_SESSIONID = "session_id_";

    private static final String PARAM_APPVERSION = "_appVersion";

    private static final int DEFAULT_ATTEMPTS = 3;

    private static final Object LOCK_SESSION = new Object();

    private final Executor asyncExecutor;

    private final Context mContext;
    private final TheKey mTheKey;
    private final String prefFile;
    private final String guid;
    private final Uri apiUri;
    private final String appVersion;
    private boolean includeAppVersion = false;
    private boolean allowGuest = false;

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final int apiUriResource) {
        this(context, thekey, prefFile, apiUriResource, null);
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final int apiUriResource, final String guid) {
        this(context, thekey, prefFile, context.getString(apiUriResource), guid);
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final String apiUri) {
        this(context, thekey, prefFile, apiUri, null);
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final String apiUri, final String guid) {
        this(context, thekey, prefFile, Uri.parse(apiUri.endsWith("/") ? apiUri : apiUri + "/"), guid);
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile, final Uri apiUri,
                                final String guid) {
        mContext = context;
        mTheKey = thekey;
        this.prefFile = prefFile;
        this.guid = guid;
        this.apiUri = apiUri;
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

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(this.prefFile, Context.MODE_PRIVATE);
    }

    private String getActiveGuid() {
        String guid = this.guid;
        if (guid == null) {
            guid = mTheKey.getGuid();

            if (guid == null && this.allowGuest) {
                guid = "GUEST";
            }
        }

        return guid;
    }

    private Session getSession(final String guid) {
        assert guid != null;

        // look up the session
        final String name = PREF_SESSIONID + guid;
        final SharedPreferences prefs = this.getPrefs();
        synchronized (LOCK_SESSION) {
            final Session session = new Session(guid, prefs.getString(name, null));
            return session.id != null ? session : null;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void storeSession(final Session session) {
        assert session != null;
        final String name = PREF_SESSIONID + session.guid;
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        prefs.putString(name, session.id);

        synchronized (LOCK_SESSION) {
            // store updates
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                prefs.apply();
            } else {
                prefs.commit();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void removeSession(final Session session) {
        assert session != null;
        final String name = PREF_SESSIONID + session.guid;
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        prefs.remove(name);

        synchronized (LOCK_SESSION) {
            // store updates
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                prefs.apply();
            } else {
                prefs.commit();
            }
        }
    }

    private Session establishSession(final String guid) throws ApiSocketException {
        assert guid != null;

        final String sessionId;
        if ("GUEST".equals(guid) && this.allowGuest) {
            sessionId = this.guestLogin();
        } else {
            // get the service to retrieve a ticket for
            final String service = this.getService();

            // get a ticket for the specified service
            final Pair<String, TheKey.Attributes> ticket;
            try {
                ticket = mTheKey.getTicketAndAttributes(service);
            } catch (final TheKeySocketException e) {
                throw new ApiSocketException(e);
            }

            // short-circuit if we don't have a valid ticket
            if (ticket == null || !guid.equals(ticket.second.getGuid())) {
                return null;
            }

            // login to the hub
            sessionId = this.login(ticket.first);
        }

        // create a session object
        final Session session = new Session(guid, sessionId);
        this.storeSession(session);

        // return the newly established session
        return session.id != null ? session : null;
    }

    @Deprecated
    protected final HttpURLConnection apiGetRequest(final String path)
            throws ApiSocketException, InvalidSessionApiException {
        return this.sendRequest(new Request(path));
    }

    @Deprecated
    protected final HttpURLConnection apiGetRequest(final boolean useSession, final String path)
            throws ApiSocketException, InvalidSessionApiException {
        final Request request = new Request(path);
        request.useSession = useSession;
        return this.sendRequest(request);
    }

    @Deprecated
    protected final HttpURLConnection apiGetRequest(final String path, final Collection<Pair<String, String>> params,
                                                    final boolean replaceParams)
            throws ApiSocketException, InvalidSessionApiException {
        final Request request = new Request(path);
        request.params.addAll(params);
        request.replaceParams = replaceParams;
        return this.sendRequest(request);
    }

    @Deprecated
    protected final HttpURLConnection apiGetRequest(final boolean useSession, final String path,
                                                    final Collection<Pair<String, String>> params,
                                                    final boolean replaceParams)
            throws ApiSocketException, InvalidSessionApiException {
        final Request request = new Request(path);
        request.useSession = useSession;
        request.params.addAll(params);
        request.replaceParams = replaceParams;
        return this.sendRequest(request);
    }

    protected final HttpURLConnection sendRequest(final Request request)
            throws ApiSocketException, InvalidSessionApiException {
        return this.sendRequest(request, DEFAULT_ATTEMPTS);
    }

    protected final HttpURLConnection sendRequest(final Request request, final int attempts)
            throws ApiSocketException, InvalidSessionApiException {
        try {
            try {
                // build the request uri
                final Uri.Builder uri = this.apiUri.buildUpon();
                final String guid = getActiveGuid();
                Session session = null;
                if (request.useSession) {
                    // short-circuit if we will be unable to get a valid session
                    if (guid == null) {
                        throw new InvalidSessionApiException();
                    }

                    // get the session, establish a session if one doesn't exist or if we have a stale session
                    synchronized (LOCK_SESSION) {
                        session = this.getSession(guid);
                        if (session == null) {
                            session = this.establishSession(guid);
                        }
                    }

                    // throw an InvalidSessionApiException if we don't have a valid session
                    if (session == null) {
                        throw new InvalidSessionApiException();
                    }

                    // use the current sessionId in the url
                    uri.appendPath(session.id);
                }
                uri.appendEncodedPath(request.path);
                if(this.includeAppVersion) {
                    uri.appendQueryParameter(PARAM_APPVERSION, this.appVersion);
                }
                if (request.params.size() > 0) {
                    if (request.replaceParams) {
                        final List<String> keys = new ArrayList<String>();
                        for (final Pair<String, String> param : request.params) {
                            keys.add(param.first);
                        }
                        UriUtils.removeQueryParams(uri, keys.toArray(new String[keys.size()]));
                    }
                    for (final Pair<String, String> param : request.params) {
                        uri.appendQueryParameter(param.first, param.second);
                    }
                }

                // build base request object
                final HttpURLConnection conn = (HttpURLConnection) new URL(uri.build().toString()).openConnection();
                conn.setRequestMethod(request.method);
                if (request.accept != null) {
                    conn.addRequestProperty("Accept", request.accept.type);
                }
                if (request.contentType != null) {
                    conn.addRequestProperty("Content-Type", request.contentType.type);
                }
                conn.setInstanceFollowRedirects(request.followRedirects);

                // POST/PUT requests
                if ("POST".equals(request.method) || "PUT".equals(request.method)) {
                    conn.setDoOutput(true);
                    final byte[] data = request.content != null ? request.content : new byte[0];
                    conn.setFixedLengthStreamingMode(data.length);
                    conn.setUseCaches(false);
                    OutputStream out = null;
                    try {
                        out = conn.getOutputStream();
                        out.write(data);
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }

                // no need to explicitly execute, accessing the response triggers the execute

                // check for an expired session
                if (request.useSession && conn.getResponseCode() == HTTP_UNAUTHORIZED) {
                    // determine the type of auth requested
                    String auth = conn.getHeaderField("WWW-Authenticate");
                    if (auth != null) {
                        auth = auth.trim();
                        int i = auth.indexOf(" ");
                        if (i != -1) {
                            auth = auth.substring(0, i);
                        }
                        auth = auth.toUpperCase(Locale.US);
                    } else {
                        // there isn't an auth header, so assume it is the CAS
                        // scheme
                        auth = "CAS";
                    }

                    // the 401 is requesting CAS auth, assume session is invalid
                    if ("CAS".equals(auth)) {
                        // reset the session
                        synchronized (LOCK_SESSION) {
                            // only reset if this is still the same session
                            assert session != null;
                            final Session current = this.getSession(guid);
                            if (current != null && session.id.equals(current.id)) {
                                this.removeSession(session);
                            }
                        }

                        // throw an invalid session exception
                        throw new InvalidSessionApiException();
                    }
                }

                // return the connection for method specific handling
                return conn;
            } catch (final MalformedURLException e) {
                throw new RuntimeException("unexpected exception", e);
            } catch (final IOException e) {
                throw new ApiSocketException(e);
            }
        } catch (final InvalidSessionApiException e) {
            // retry request on invalid session exceptions
            if (attempts > 0) {
                return this.sendRequest(request, attempts - 1);
            }

            // propagate the exception
            throw e;
        } catch (final ApiSocketException e) {
            // retry request on socket exceptions (maybe spotty internet)
            if (attempts > 0) {
                return this.sendRequest(request, attempts - 1);
            }

            // propagate the exception
            throw e;
        }
    }

    public String getService() throws ApiSocketException {
        final Request request = new Request("auth/service");
        request.useSession = false;
        request.accept = Request.MediaType.TEXT_PLAIN;
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            if (conn != null && conn.getResponseCode() == HTTP_OK) {
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

    public String login(final String ticket) throws ApiSocketException {
        // don't attempt to login if we don't have a ticket
        if (ticket == null) {
            return null;
        }

        // build request
        final Request request = new Request("auth/login");
        request.useSession = false;
        request.method = "POST";
        request.accept = Request.MediaType.TEXT_PLAIN;
        try {
            request.setContent(Request.MediaType.APPLICATION_FORM_URLENCODED,
                               ("ticket=" + URLEncoder.encode(ticket, "UTF-8")).getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("unexpected error, UTF-8 encoding doesn't exist?", e);
        }

        // issue login request
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            // was this a valid login
            if (conn != null && conn.getResponseCode() == HTTP_OK) {
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

    public String guestLogin() throws ApiSocketException {
        // build request
        final Request request = new Request("auth/login");
        request.useSession = false;
        request.method = "POST";
        request.accept = Request.MediaType.TEXT_PLAIN;
        request.setContent(Request.MediaType.APPLICATION_FORM_URLENCODED, "guest=true");

        // issue login request
        HttpURLConnection conn = null;
        try {
            conn = this.sendRequest(request);

            // was this a valid login
            if (conn != null && conn.getResponseCode() == HTTP_OK) {
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

    private static final class Session {
        private final String id;
        private final String guid;

        private Session(final String guid, final String id) {
            assert guid != null;
            this.guid = guid;
            this.id = id;
        }
    }

    /**
     * class that represents a request being sent to the api
     */
    protected static class Request {
        public enum MediaType {
            APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"), APPLICATION_JSON("application/json"),
            APPLICATION_XML("application/xml"), TEXT_PLAIN("text/plain");

            private final String type;

            private MediaType(final String type) {
                this.type = type;
            }
        }

        public String method = "GET";

        // uri attributes
        public boolean useSession = true;
        private final String path;
        public final Collection<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        public boolean replaceParams = false;

        // POST/PUT data
        private MediaType contentType = null;
        private byte[] content = null;

        // miscellaneous attributes
        public MediaType accept = null;
        public boolean followRedirects = false;

        public Request(final String path) {
            assert path != null : "request path cannot be null";
            this.path = path;
        }

        protected void setContent(final MediaType type, final byte[] data) {
            this.contentType = type;
            this.content = data;
        }

        protected void setContent(final MediaType type, final String data) {
            try {
                this.setContent(type, data != null ? data.getBytes("UTF-8") : null);
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("unexpected error, UTF-8 encoding isn't present", e);
            }
        }
    }
}
