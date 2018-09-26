package org.ccci.gto.android.common.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.api.AbstractTheKeyApi.ExecutionContext;
import org.ccci.gto.android.common.api.AbstractTheKeyApi.Request;
import org.ccci.gto.android.common.util.HttpHeaderUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

import me.thekey.android.TheKey;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public abstract class AbstractTheKeyApi<R extends Request<C, S>, C extends ExecutionContext<S>, S extends TheKeySession>
        extends AbstractApi<R, C, S> {
    private static final String PREF_CACHED_SERVICE = "service";

    @NonNull
    protected final TheKey mTheKey;
    @Nullable
    protected final String mGuid;

    protected AbstractTheKeyApi(@NonNull final Context context, @NonNull final TheKey theKey,
                                @NonNull final String baseUri) {
        this(context, theKey, baseUri, null, null);
    }

    protected AbstractTheKeyApi(@NonNull final Context context, @NonNull final TheKey theKey,
                                @NonNull final String baseUri, @Nullable final String prefFile) {
        this(context, theKey, baseUri, prefFile, null);
    }

    protected AbstractTheKeyApi(@NonNull final Context context, @NonNull final TheKey theKey,
                                @NonNull final String baseUri, @Nullable final String prefFile,
                                @Nullable final String guid) {
        super(context, baseUri, prefFile);
        mTheKey = theKey;
        mGuid = guid;
        init();
    }

    protected AbstractTheKeyApi(@NonNull final Context context, @NonNull final TheKey theKey,
                                @NonNull final Uri baseUri) {
        this(context, theKey, baseUri, null, null);
    }

    protected AbstractTheKeyApi(@NonNull final Context context, @NonNull final TheKey theKey,
                                @NonNull final Uri baseUri, @Nullable final String prefFile,
                                @Nullable final String guid) {
        super(context, baseUri, prefFile);
        mTheKey = theKey;
        mGuid = guid;
        init();
    }

    private void init() {
        // initialize service if there isn't one already defined
        if (getCachedService() == null) {
            setCachedService(getDefaultService());
        }
    }

    @Nullable
    protected String getActiveGuid() {
        return mGuid != null ? mGuid : mTheKey.getDefaultSessionGuid();
    }

    @Nullable
    protected String getService() throws ApiException {
        // short-circuit if we have a cached service
        String service = getCachedService();
        if (service != null) {
            return service;
        }

        // check for a default service
        service = getDefaultService();

        // cache any found service before returning
        if (service != null) {
            this.setCachedService(service);
        }
        return service;
    }

    @Nullable
    protected String getDefaultService() {
        return null;
    }

    @Nullable
    protected final String getCachedService() {
        return this.getPrefs().getString(PREF_CACHED_SERVICE, null);
    }

    protected final void setCachedService(@Nullable final String service) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        prefs.putString(PREF_CACHED_SERVICE, service);
        prefs.apply();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    protected C newExecutionContext() {
        return (C) new ExecutionContext<S>();
    }

    /* BEGIN request lifecycle events */

    @Override
    protected void onPrepareSession(@NonNull final R request) throws ApiException {
        super.onPrepareSession(request);

        // throw an exception if there isn't an active guid
        assert request.context != null;
        request.context.guid = getActiveGuid();
        if (request.context.guid == null) {
            throw new InvalidSessionApiException();
        }
    }

    @Override
    protected void onProcessResponse(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws ApiException, IOException {
        // update cached service based on any CAS challenge
        final HttpHeaderUtils.Challenge challenge = this.getCasAuthChallenge(conn);
        if (challenge != null) {
            // update service if one is returned
            final String service = challenge.getParameterValue("service");
            if (service != null && service.length() > 0) {
                this.setCachedService(service);
            }
        }

        // continue processing the response
        super.onProcessResponse(conn, request);
    }

    @Override
    protected boolean isSessionInvalid(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws IOException {
        return super.isSessionInvalid(conn, request) || getCasAuthChallenge(conn) != null;
    }

    /* END request lifecycle events */

    @Nullable
    protected final HttpHeaderUtils.Challenge getCasAuthChallenge(@NonNull final HttpURLConnection conn)
            throws IOException {
        // Check 401 Unauthorized responses to see if it's because of needing CAS authentication
        if (conn.getResponseCode() == HTTP_UNAUTHORIZED) {
            // Check the WWW-Authenticate challenge
            final String auth = conn.getHeaderField("WWW-Authenticate");
            if (auth != null) {
                // is this a CAS WWW-Authenticate challenge
                final HttpHeaderUtils.Challenge challenge = HttpHeaderUtils.parseChallenge(auth);
                if ("CAS".equals(challenge.getScheme())) {
                    return challenge;
                }
            }
        }

        return null;
    }

    public static class ExecutionContext<S extends TheKeySession> extends AbstractApi.ExecutionContext<S> {
        @Nullable
        public String guid = null;
    }

    public static class Request<C extends ExecutionContext<S>, S extends TheKeySession>
            extends AbstractApi.Request<C, S> {
        public Request(@NonNull final String path) {
            super(path);
            // use sessions by default for The Key protected APIs
            this.useSession = true;
        }
    }
}
