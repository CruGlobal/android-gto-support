package org.ccci.gto.android.common.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.api.AbstractApi.Request;
import org.ccci.gto.android.common.api.AbstractApi.Session;
import org.ccci.gto.android.common.util.IOUtils;
import org.ccci.gto.android.common.util.UriUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractApi<R extends Request<S>, S extends Session> {
    private static final int DEFAULT_ATTEMPTS = 3;
    protected static final String PREF_SESSION_BASE_NAME = "session";

    protected final Object LOCK_SESSION = new Object();

    @NonNull
    private final Context mContext;

    @NonNull
    private final Uri mBaseUri;
    @NonNull
    private final String mPrefFile;

    protected AbstractApi(@NonNull final Context context, @NonNull final String baseUri) {
        this(context, baseUri, null);
    }

    protected AbstractApi(@NonNull final Context context, @NonNull final String baseUri,
                          @Nullable final String prefFile) {
        this(context, Uri.parse(baseUri.endsWith("/") ? baseUri : baseUri + "/"), prefFile);
    }

    protected AbstractApi(@NonNull final Context context, @NonNull final Uri baseUri) {
        this(context, baseUri, null);
    }

    protected AbstractApi(@NonNull final Context context, @NonNull final Uri baseUri, @Nullable final String prefFile) {
        this.mContext = context;
        this.mBaseUri = baseUri;
        this.mPrefFile = prefFile != null ? prefFile : getClass().getSimpleName();
    }

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE);
    }

    @NonNull
    protected final HttpURLConnection sendRequest(@NonNull final R request) throws ApiException {
        return this.sendRequest(request, DEFAULT_ATTEMPTS);
    }

    @NonNull
    protected final HttpURLConnection sendRequest(@NonNull final R request, final int attempts)
            throws ApiException {
        try {
            HttpURLConnection conn = null;
            boolean successful = false;
            try {
                // load/establish the session if we are using sessions
                if (request.useSession) {
                    // prepare for the session
                    this.onPrepareSession(request);

                    // get the session, establish a session if one doesn't exist or if we have a stale session
                    synchronized (LOCK_SESSION) {
                        request.session = loadSession(request);
                        if (request.session == null) {
                            request.session = this.establishSession(request);

                            // save the newly established session
                            if (request.session != null) {
                                this.saveSession(request.session);
                            }
                        }
                    }

                    // throw an exception if we don't have a valid session
                    if (request.session == null) {
                        throw new InvalidSessionApiException();
                    }
                }

                // build the request uri
                final Uri.Builder uri = mBaseUri.buildUpon();
                onPrepareUri(uri, request);
                final URL url;
                try {
                    url = new URL(uri.build().toString());
                } catch (final MalformedURLException e) {
                    throw new RuntimeException("invalid Request URL: " + uri.build().toString(), e);
                }

                // prepare the request
                conn = (HttpURLConnection) url.openConnection();
                onPrepareRequest(conn, request);

                // POST/PUT requests
                if (request.method == Request.Method.POST || request.method == Request.Method.PUT) {
                    conn.setDoOutput(true);
                    final byte[] data = request.content != null ? request.content : new byte[0];
                    conn.setFixedLengthStreamingMode(data.length);
                    conn.setUseCaches(false);
                    OutputStream out = null;
                    try {
                        out = conn.getOutputStream();
                        out.write(data);
                    } finally {
                        // XXX: don't use IOUtils.closeQuietly, we want exceptions thrown
                        if (out != null) {
                            out.close();
                        }
                    }
                }

                // no need to explicitly execute, accessing the response triggers the execute

                // check for an invalid session
                if (request.useSession && this.isSessionInvalid(conn, request)) {
                    // reset the session
                    synchronized (LOCK_SESSION) {
                        // only reset if this is still the same session
                        final Session current = this.loadSession(request);
                        if (current != null && current.equals(request.session)) {
                            this.deleteSession(request.session);
                        }
                    }

                    // throw an invalid session exception
                    throw new InvalidSessionApiException();
                }

                // process the response
                onProcessResponse(conn, request);

                // return the connection for method specific handling
                successful = true;
                return conn;
            } catch (final IOException e) {
                throw new ApiSocketException(e);
            } finally {
                // close a potentially open connection if we weren't successful
                if (!successful) {
                    IOUtils.closeQuietly(conn);
                }

                // clear out the session that was loaded & used
                request.session = null;

                // cleanup any request specific data
                onCleanupRequest(request);
            }
        } catch (final ApiException e) {
            // retry request on an API exception
            if (attempts > 0) {
                return this.sendRequest(request, attempts - 1);
            }

            // propagate the exception
            throw e;
        }
    }

    @NonNull
    protected final Request.Parameter param(@NonNull final String name, @NonNull final String value) {
        return new Request.Parameter(name, value);
    }

    protected final Request.Parameter param(@NonNull final String name, final int value) {
        return new Request.Parameter(name, Integer.toString(value));
    }

    @Nullable
    protected S loadSession(@NonNull final R request) {
        // load a pre-existing session
        final SharedPreferences prefs = this.getPrefs();
        final S session;
        synchronized (LOCK_SESSION) {
            session = this.loadSession(prefs, request);
        }

        // only return valid sessions
        return session != null && session.id != null ? session : null;
    }

    @Nullable
    protected abstract S loadSession(@NonNull SharedPreferences prefs, @NonNull R request);

    @Nullable
    protected S establishSession(@NonNull final R request) throws ApiException {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void saveSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        session.save(prefs);

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
    private void deleteSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        session.delete(prefs);

        synchronized (LOCK_SESSION) {
            // store updates
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                prefs.apply();
            } else {
                prefs.commit();
            }
        }
    }

    protected boolean isSessionInvalid(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws IOException {
        return false;
    }

    /* BEGIN request lifecycle events */

    protected void onPrepareSession(@NonNull final R request) throws ApiException {
    }

    protected void onPrepareUri(@NonNull final Uri.Builder uri, @NonNull final R request)
            throws ApiException {
        // build the request uri
        uri.appendEncodedPath(request.path);
        if (request.params.size() > 0) {
            if (request.replaceParams) {
                final List<String> keys = new ArrayList<>();
                for (final Request.Parameter param : request.params) {
                    keys.add(param.name);
                }
                UriUtils.removeQueryParams(uri, keys.toArray(new String[keys.size()]));
            }
            for (final Request.Parameter param : request.params) {
                uri.appendQueryParameter(param.name, param.value);
            }
        }
    }

    protected void onPrepareRequest(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
        // build base request object
        conn.setRequestMethod(request.method.toString());
        if (request.accept != null) {
            conn.addRequestProperty("Accept", request.accept.type);
        }
        if (request.contentType != null) {
            conn.addRequestProperty("Content-Type", request.contentType.type);
        }
        conn.setInstanceFollowRedirects(request.followRedirects);
    }

    protected void onProcessResponse(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
    }

    protected void onCleanupRequest(@NonNull final R request) {
    }

    /* END request lifecycle events */

    protected static class Session {
        @NonNull
        private final String baseAttrName;

        @Nullable
        public final String id;

        protected Session(@Nullable final String id) {
            this(id, PREF_SESSION_BASE_NAME);
        }

        protected Session(@Nullable final String id, @Nullable final String baseAttrName) {
            this.baseAttrName = baseAttrName != null ? baseAttrName : PREF_SESSION_BASE_NAME;
            this.id = id;
        }

        protected Session(@NonNull final SharedPreferences prefs) {
            this(prefs, null);
        }

        protected Session(@NonNull final SharedPreferences prefs, @Nullable final String baseAttrName) {
            this.baseAttrName = baseAttrName != null ? baseAttrName : PREF_SESSION_BASE_NAME;
            this.id = prefs.getString(getPrefAttrName("id"), null);
        }

        protected final String getPrefAttrName(@NonNull final String type) {
            return baseAttrName + "." + type;
        }

        protected void save(@NonNull final SharedPreferences.Editor prefs) {
            prefs.putString(getPrefAttrName("id"), this.id);
        }

        protected void delete(@NonNull final SharedPreferences.Editor prefs) {
            prefs.remove(getPrefAttrName("id"));
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Session that = (Session) o;
            return baseAttrName.equals(that.baseAttrName) && !(id != null ? !id.equals(that.id) : that.id != null);
        }
    }

    protected static class Request<S extends Session> {
        public enum Method {GET, POST, PUT, DELETE}

        public enum MediaType {
            APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"), APPLICATION_JSON("application/json"),
            APPLICATION_XML("application/xml"), TEXT_PLAIN("text/plain");

            final String type;

            private MediaType(final String type) {
                this.type = type;
            }
        }

        protected static final class Parameter {
            final String name;
            final String value;

            public Parameter(@NonNull final String name, @NonNull final String value) {
                this.name = name;
                this.value = value;
            }
        }

        @NonNull
        public Method method = Method.GET;

        // uri attributes
        @NonNull
        final String path;
        public final Collection<Parameter> params = new ArrayList<>();
        public boolean replaceParams = false;

        // POST/PUT data
        @Nullable
        MediaType contentType = null;
        @Nullable
        byte[] content = null;

        // session attributes
        public boolean useSession = false;
        @Nullable
        public S session = null;

        // miscellaneous attributes
        @Nullable
        public MediaType accept = null;
        public boolean followRedirects = false;

        public Request(@NonNull final String path) {
            this.path = path;
        }

        protected void setContent(@Nullable final MediaType type, @Nullable final byte[] data) {
            this.contentType = type;
            this.content = data;
        }

        protected void setContent(@Nullable final MediaType type, @Nullable final String data) {
            try {
                this.setContent(type, data != null ? data.getBytes("UTF-8") : null);
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("unexpected error, UTF-8 encoding isn't present", e);
            }
        }

        protected void setContent(@Nullable final JSONArray json) {
            this.setContent(MediaType.APPLICATION_JSON, json != null ? json.toString() : null);
        }
    }
}
