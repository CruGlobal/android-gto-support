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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import me.thekey.android.TheKey;
import me.thekey.android.TheKeySocketException;

public abstract class AbstractGtoSmxApi {
    private static final String PREF_SESSIONID = "session_id";
    private static final String PREF_SESSIONGUID = "session_guid";

    private static final Object LOCK_SESSION = new Object();

    private final String prefFile;
    private final Context context;
    private final TheKey thekey;
    private final Uri apiUri;

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile, final int apiUriResource) {
        this(context, thekey, prefFile, context.getString(apiUriResource));
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile,
                                final String apiUri) {
        this(context, thekey, prefFile, Uri.parse(apiUri.endsWith("/") ? apiUri : apiUri + "/"));
    }

    protected AbstractGtoSmxApi(final Context context, final TheKey thekey, final String prefFile, final Uri apiUri) {
        this.context = context;
        this.thekey = thekey;
        this.prefFile = prefFile;
        this.apiUri = apiUri;
    }

    protected SharedPreferences getPrefs() {
        return this.context.getSharedPreferences(this.prefFile, Context.MODE_PRIVATE);
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
                final Pair<String, TheKey.Attributes> ticket = this.thekey.getTicketAndAttributes(service);

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

    protected final HttpURLConnection apiGetRequest(final String path)
            throws ApiSocketException, InvalidSessionApiException {
        return this.apiGetRequest(path, Collections.<Pair<String, String>>emptyList(), false);
    }

    protected final HttpURLConnection apiGetRequest(final boolean useSession, final String path)
            throws ApiSocketException, InvalidSessionApiException {
        return this.apiGetRequest(useSession, path, Collections.<Pair<String, String>>emptyList(), false);
    }

    protected final HttpURLConnection apiGetRequest(final String path, final Collection<Pair<String, String>> params,
                                                    final boolean replaceParams)
            throws ApiSocketException, InvalidSessionApiException {
        return this.apiGetRequest(true, path, params, replaceParams);
    }

    protected final HttpURLConnection apiGetRequest(final boolean useSession, final String path,
                                                    final Collection<Pair<String, String>> params,
                                                    final boolean replaceParams)
            throws ApiSocketException, InvalidSessionApiException {
        return this.apiGetRequest(useSession, path, params, replaceParams, 3);
    }

    protected final HttpURLConnection apiGetRequest(final boolean useSession, final String path,
                                                    final Collection<Pair<String, String>> params,
                                                    final boolean replaceParams, final int attempts)
            throws ApiSocketException, InvalidSessionApiException {
        try {
            try {
                // build the request uri
                Pair<String, String> session = null;
                final Uri.Builder uri = this.apiUri.buildUpon();
                if (useSession) {
                    // get the session, establish a session if one doesn't exist or if we have a stale session
                    synchronized (LOCK_SESSION) {
                        session = this.getSession();
                        if (session == null || session.first == null || session.second == null ||
                                !session.second.equals(this.thekey.getGuid())) {
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
                uri.appendEncodedPath(path);
                if (params.size() > 0) {
                    if (replaceParams) {
                        final List<String> keys = new ArrayList<String>();
                        for (final Pair<String, String> param : params) {
                            keys.add(param.first);
                        }
                        UriUtils.removeQueryParams(uri, keys.toArray(new String[keys.size()]));
                    }
                    for (final Pair<String, String> param : params) {
                        uri.appendQueryParameter(param.first, param.second);
                    }
                }

                // open the connection
                final HttpURLConnection conn = (HttpURLConnection) new URL(uri.build().toString()).openConnection();
                conn.setInstanceFollowRedirects(false);

                // check for an expired session
                if (useSession && conn.getResponseCode() == HTTP_UNAUTHORIZED) {
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
                return this.apiGetRequest(useSession, path, params, replaceParams, attempts - 1);
            }

            // propagate the exception
            throw e;
        } catch (final ApiSocketException e) {
            // retry request on socket exceptions (maybe spotty internet)
            if (attempts > 0) {
                return this.apiGetRequest(useSession, path, params, replaceParams, attempts - 1);
            }

            // propagate the exception
            throw e;
        }
    }

    public String getService() throws ApiSocketException {
        HttpURLConnection conn = null;
        try {
            conn = this.apiGetRequest(false, "auth/service");

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

        HttpURLConnection conn = null;
        try {
            // issue login request
            final String uri = this.apiUri.buildUpon().appendEncodedPath("auth/login").build().toString();
            conn = (HttpURLConnection) new URL(uri).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            final byte[] data = ("ticket=" + URLEncoder.encode(ticket, "UTF-8")).getBytes("UTF-8");
            conn.setFixedLengthStreamingMode(data.length);
            conn.getOutputStream().write(data);

            // was this a valid login
            if (conn != null && conn.getResponseCode() == HTTP_OK) {
                // the sessionId is returned as the body of the response
                return IOUtils.readString(conn.getInputStream());
            }

            return null;
        } catch (final MalformedURLException e) {
            throw new RuntimeException("unexpected exception", e);
        } catch (final IOException e) {
            throw new ApiSocketException(e);
        } finally {
            IOUtils.closeQuietly(conn);
        }
    }
}
