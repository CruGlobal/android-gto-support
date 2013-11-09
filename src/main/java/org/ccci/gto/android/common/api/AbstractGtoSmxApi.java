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
    private static final String PREF_SESSIONID = "session_id";
    private static final String PREF_SESSIONGUID = "session_guid";

    private static final String PARAM_APPVERSION = "_appVersion";

    private static final int DEFAULT_ATTEMPTS = 3;

    private static final Object LOCK_SESSION = new Object();

    private final Executor asyncExecutor;

    private final Context mContext;
    private final TheKey mTheKey;
    private final String prefFile;
    private final Uri apiUri;
    private final String appVersion;
    private boolean includeAppVersion = false;

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final int apiUriResource) {
        this(context, thekey, prefFile, context.getString(apiUriResource));
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final String apiUri) {
        this(context, thekey, prefFile, Uri.parse(apiUri.endsWith("/") ? apiUri : apiUri + "/"));
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile, final Uri apiUri) {
        mContext = context;
        mTheKey = thekey;
        this.prefFile = prefFile;
        this.apiUri = apiUri;
        this.asyncExecutor = Executors.newFixedThreadPool(1);
        if (this.asyncExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) this.asyncExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                ((ThreadPoolExecutor) this.asyncExecutor).allowCoreThreadTimeOut(true);
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

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(this.prefFile, Context.MODE_PRIVATE);
    }

    private Pair<String, String> getSession() {
        synchronized (LOCK_SESSION) {
            final SharedPreferences prefs = this.getPrefs();
            return Pair.create(prefs.getString(PREF_SESSIONID, null), prefs.getString(PREF_SESSIONGUID, null));
        }
    }

    private String getSessionId() {
        return this.getSession().first;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void setSession(final Pair<String, String> session) {
        synchronized (LOCK_SESSION) {
            final SharedPreferences.Editor prefs = this.getPrefs().edit();
            prefs.putString(PREF_SESSIONID, session != null ? session.first : null);
            prefs.putString(PREF_SESSIONGUID, session != null ? session.second : null);

            // store updates
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                prefs.apply();
            } else {
                prefs.commit();
            }
        }
    }

    private Pair<String, String> establishSession() throws ApiSocketException {
        synchronized (LOCK_SESSION) {
            try {
                // get the service to retrieve a ticket for
                final String service = this.getService();

                // get a ticket for the specified service
                final Pair<String, TheKey.Attributes> ticket = mTheKey.getTicketAndAttributes(service);

                // login to the hub
                final Pair<String, String> session = Pair.create(this.login(ticket.first), ticket.second.getGuid());
                this.setSession(session);

                // return the newly established session
                return session;
            } catch (TheKeySocketException e) {
                throw new ApiSocketException(e);
            }
        }
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
                Pair<String, String> session = null;
                final Uri.Builder uri = this.apiUri.buildUpon();
                if (request.useSession) {
                    // get the session, establish a session if one doesn't exist or if we have a stale session
                    synchronized (LOCK_SESSION) {
                        session = this.getSession();
                        if (session == null || session.first == null || session.second == null ||
                                !session.second.equals(mTheKey.getGuid())) {
                            session = this.establishSession();
                        }
                    }

                    // throw an InvalidSessionApiException if we don't have a valid session
                    if (session == null || session.first == null || session.second == null) {
                        throw new InvalidSessionApiException();
                    }

                    // use the current sessionId in the url
                    uri.appendPath(session.first);
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
                if (request.contentType != null) {
                    conn.addRequestProperty("Content-Type", request.contentType);
                }
                conn.setInstanceFollowRedirects(false);

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
                            if (session.equals(this.getSession())) {
                                this.setSession(null);
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
        try {
            request.setContent("application/x-www-form-urlencoded",
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

    public final void async(final Runnable task) {
        this.asyncExecutor.execute(task);
    }

    /**
     * class that represents a request being sent to the api
     */
    protected static class Request {
        public String method = "GET";

        // uri attributes
        public boolean useSession = true;
        private final String path;
        public final Collection<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        public boolean replaceParams = false;

        // POST/PUT data
        private String contentType = null;
        private byte[] content = null;

        public Request(final String path) {
            assert path != null : "request path cannot be null";
            this.path = path;
        }

        protected void setContent(final String type, final byte[] data) {
            this.contentType = type;
            this.content = data;
        }

        protected void setContent(final String type, final String data) {
            try {
                this.setContent(type, data != null ? data.getBytes("UTF-8") : null);
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("unexpected error, UTF-8 encoding isn't present", e);
            }
        }
    }
}
