package org.ccci.gto.android.common.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.common.io.Closer;

import org.ccci.gto.android.common.api.AbstractApi.ExecutionContext;
import org.ccci.gto.android.common.api.AbstractApi.Request;
import org.ccci.gto.android.common.api.AbstractApi.Request.MediaType;
import org.ccci.gto.android.common.api.AbstractApi.Request.Parameter;
import org.ccci.gto.android.common.util.IOUtils;
import org.ccci.gto.android.common.util.UriUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
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
import java.util.UUID;

public abstract class AbstractApi<R extends Request<C, S>, C extends ExecutionContext<S>, S extends Session> {
    private static final int DEFAULT_ATTEMPTS = 3;

    protected final Object mLockSession = new Object();

    @NonNull
    protected final Context mContext;

    @NonNull
    protected final Uri mBaseUri;
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
        mContext = context;
        mBaseUri = baseUri;
        mPrefFile = prefFile != null ? prefFile : getClass().getSimpleName();
    }

    @NonNull
    protected final SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE);
    }

    @NonNull
    @WorkerThread
    protected final HttpURLConnection sendRequest(@NonNull final R request) throws ApiException {
        return this.sendRequest(request, DEFAULT_ATTEMPTS);
    }

    @NonNull
    @WorkerThread
    protected final HttpURLConnection sendRequest(@NonNull final R request, final int attempts)
            throws ApiException {
        try {
            // create a new execution context for this request
            request.context = newExecutionContext();

            // process request
            HttpURLConnection conn = null;
            boolean successful = false;
            try {
                // load/establish the session if we are using sessions
                if (request.useSession) {
                    // prepare for the session
                    onPrepareSession(request);

                    // get the session, establish a session if one doesn't exist or if we have a stale session
                    synchronized (mLockSession) {
                        request.context.session = loadSession(request);
                        if (request.context.session == null) {
                            request.context.session = establishSession(request);

                            // save the newly established session
                            if (request.context.session != null && request.context.session.isValid()) {
                                saveSession(request.context.session);
                            }
                        }
                    }

                    // throw an exception if we don't have a valid session
                    if (request.context.session == null) {
                        throw new InvalidSessionApiException();
                    }
                }

                // build the request uri
                final Uri.Builder uri = mBaseUri.buildUpon();
                onPrepareUri(uri, request);
                try {
                    request.context.url = new URL(uri.build().toString());
                } catch (final MalformedURLException e) {
                    throw new RuntimeException("invalid Request URL: " + uri.build().toString(), e);
                }

                // prepare the request
                conn = (HttpURLConnection) request.context.url.openConnection();
                onPrepareRequest(conn, request);

                // send any request data
                onSendRequestData(conn, request);

                // no need to explicitly execute, accessing the response triggers the execute

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
    protected final Parameter param(@NonNull final String name, @NonNull final String value) {
        return new Parameter(name, value);
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, final int value) {
        return new Parameter(name, Integer.toString(value));
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, final long value) {
        return new Parameter(name, Long.toString(value));
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, final boolean value) {
        return new Parameter(name, Boolean.toString(value));
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, @NonNull final File file) {
        return param(name, file, null, null);
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, @NonNull final File file,
                                    @Nullable final MediaType type) {
        return param(name, file, null, type);
    }

    @NonNull
    protected final Parameter param(@NonNull final String name, @NonNull final File file,
                                    @Nullable final String fileName, @Nullable final MediaType type) {
        return new Parameter(name, file, fileName, type);
    }

    /**
     * Creates a new ExecutionContext object. This needs to be overridden when a subclass overrides the
     * ExecutionContext.
     *
     * @return
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected C newExecutionContext() {
        return (C) new ExecutionContext<S>();
    }

    @Nullable
    protected S loadSession(@NonNull final R request) {
        // load a pre-existing session
        final SharedPreferences prefs = this.getPrefs();
        final S session;
        synchronized (mLockSession) {
            session = this.loadSession(prefs, request);
        }

        // only return valid sessions
        return session != null && session.isValid() ? session : null;
    }

    @Nullable
    protected abstract S loadSession(@NonNull SharedPreferences prefs, @NonNull R request);

    @Nullable
    protected S establishSession(@NonNull final R request) throws ApiException {
        return null;
    }

    private void saveSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        session.save(prefs);

        synchronized (mLockSession) {
            // store updates
            prefs.apply();
        }
    }

    private void deleteSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        session.delete(prefs);

        synchronized (mLockSession) {
            // store updates
            prefs.apply();
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
        uri.appendEncodedPath(request.mPath);
        if (request.params.size() > 0) {
            if (request.replaceParams) {
                final List<String> keys = new ArrayList<>();
                for (final Parameter param : request.params) {
                    keys.add(param.mName);
                }
                UriUtils.removeQueryParams(uri, keys.toArray(new String[keys.size()]));
            }
            for (final Parameter param : request.params) {
                if (param != null && param.mValue != null) {
                    uri.appendQueryParameter(param.mName, param.mValue);
                }
            }
        }
    }

    protected void onPrepareRequest(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
        // build base request object
        conn.setRequestMethod(request.method.toString());
        if (request.accept != null) {
            conn.addRequestProperty("Accept", request.accept.mType);
        }
        if (request.mContentType != null) {
            conn.addRequestProperty("Content-Type", request.mContentType.mType);
        }
        conn.setInstanceFollowRedirects(request.followRedirects);
    }

    protected void onSendRequestData(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
        // send data for POST/PUT requests
        if (request.method == Request.Method.POST || request.method == Request.Method.PUT) {
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            // handle multi-part forms
            if (request.mContentType == MediaType.FORM_MULTIPART) {
                final String boundary = "-------" + UUID.randomUUID().toString() + "-------";
                conn.setRequestProperty("Content-Type", request.mContentType.mType + "; boundary=" + boundary);

                final Closer closer = Closer.create();
                try {
                    final OutputStream out = closer.register(conn.getOutputStream());
                    final BufferedOutputStream buf = closer.register(new BufferedOutputStream(out));
                    final MultipartFormOutputWriter writer =
                            closer.register(new MultipartFormOutputWriter(buf, boundary));

                    // write all parameters
                    for (final Parameter param : request.form) {
                        if (param != null) {
                            writer.writeParameter(param);
                        }
                    }

                    // finish the Output
                    writer.finish();
                } catch (final Throwable t) {
                    throw closer.rethrow(t);
                } finally {
                    closer.close();
                }
            } else {
                // generate data to send
                byte[] data = request.mContent;

                // URL encoded form
                if (data == null && request.mContentType == MediaType.FORM_URLENCODED) {
                    final StringBuilder params = new StringBuilder();
                    for (final Parameter param : request.form) {
                        if (param != null && param.mValue != null) {
                            if (params.length() > 0) {
                                params.append('&');
                            }
                            params.append(URLEncoder.encode(param.mName, "UTF-8")).append('=')
                                    .append(URLEncoder.encode(param.mValue, "UTF-8"));
                        }
                    }
                    data = params.toString().getBytes("UTF-8");
                }

                // default data
                if (data == null) {
                    data = new byte[0];
                }

                // output data
                conn.setFixedLengthStreamingMode(data.length);
                final Closer closer = Closer.create();
                try {
                    final OutputStream out = closer.register(conn.getOutputStream());
                    out.write(data);
                } catch (final Throwable t) {
                    throw closer.rethrow(t);
                } finally {
                    closer.close();
                }
            }
        }
    }

    protected void onProcessResponse(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
        // check for an invalid session
        if (request.useSession && this.isSessionInvalid(conn, request)) {
            // reset the session
            synchronized (mLockSession) {
                // only reset if this is still the same session
                final S active = loadSession(request);
                final S invalid = request.context != null ? request.context.session : null;
                if (active != null && active.equals(invalid)) {
                    deleteSession(invalid);
                }
            }

            // throw an invalid session exception
            throw new InvalidSessionApiException();
        }
    }

    protected void onCleanupRequest(@NonNull final R request) {
    }

    /* END request lifecycle events */

    /**
     * Object tracking the execution context data for processing a request.
     *
     * @param <S> The session type in use
     */
    public static class ExecutionContext<S extends Session> {
        @Nullable
        public URL url = null;

        @Nullable
        public S session = null;
    }

    /**
     * Object representing a Request for this API.
     *
     * @param <C> The ExecutionContext type being used for this request
     */
    public static class Request<C extends ExecutionContext<S>, S extends Session> {
        public enum Method {
            GET, POST, PUT, DELETE
        }

        public enum MediaType {
            FORM_MULTIPART("multipart/form-data"), FORM_URLENCODED("application/x-www-form-urlencoded"),
            APPLICATION_JSON("application/json"), APPLICATION_XML("application/xml"),
            APPLICATION_OCTET_STREAM("application/octet-stream"), IMAGE_JPEG("image/jpeg"), TEXT_PLAIN("text/plain");

            final String mType;

            MediaType(final String type) {
                mType = type;
            }
        }

        public static final class Parameter {
            @NonNull
            final String mName;
            @Nullable
            final String mValue;

            @Nullable
            final File mFile;
            @Nullable
            final String mFileName;
            @Nullable
            final MediaType mType;

            Parameter(@NonNull final String name, @NonNull final String value) {
                mName = name;
                mValue = value;
                mFile = null;
                mFileName = null;
                mType = null;
            }

            Parameter(@NonNull final String name, @NonNull final File file, @Nullable final String fileName,
                      @Nullable final MediaType type) {
                mName = name;
                mValue = null;
                mFile = file;
                mFileName = fileName;
                mType = type;
            }
        }

        // the ExecutionContext for this request
        @Nullable
        public C context = null;

        @NonNull
        public Method method = Method.GET;

        // uri attributes
        @NonNull
        final String mPath;
        public final Collection<Parameter> params = new ArrayList<>();
        public boolean replaceParams = false;

        // POST/PUT data
        @Nullable
        MediaType mContentType = null;
        @Nullable
        byte[] mContent = null;
        public final Collection<Parameter> form = new ArrayList<>();

        // session attributes
        public boolean useSession = false;

        // miscellaneous attributes
        @Nullable
        public MediaType accept = null;
        public boolean followRedirects = false;

        public Request(@NonNull final String path) {
            mPath = path;
        }

        public void setContentType(@Nullable final MediaType type) {
            mContentType = type;
        }

        public void setContent(@Nullable final MediaType type, @Nullable final byte[] data) {
            mContentType = type;
            mContent = data;
        }

        public void setContent(@Nullable final MediaType type, @Nullable final String data) {
            try {
                this.setContent(type, data != null ? data.getBytes("UTF-8") : null);
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("unexpected error, UTF-8 encoding isn't present", e);
            }
        }

        public void setContent(@Nullable final JSONArray json) {
            this.setContent(MediaType.APPLICATION_JSON, json != null ? json.toString() : null);
        }

        public void setContent(@Nullable final JSONObject json) {
            this.setContent(MediaType.APPLICATION_JSON, json != null ? json.toString() : null);
        }
    }
}
