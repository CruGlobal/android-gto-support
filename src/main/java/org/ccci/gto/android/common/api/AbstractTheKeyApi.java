package org.ccci.gto.android.common.api;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.util.HttpHeaderUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;

import me.thekey.android.TheKey;

public abstract class AbstractTheKeyApi<R extends AbstractTheKeyApi.Request<S>, S extends AbstractTheKeyApi.Session>
        extends AbstractApi<R, S> {
    @NonNull
    protected final TheKey mTheKey;
    @Nullable
    protected final String mGuid;

    @Nullable
    private String mService;

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
    }

    @Nullable
    protected String getActiveGuid() {
        String guid = mGuid;
        if (guid == null) {
            guid = mTheKey.getGuid();
        }

        return guid;
    }

    @Nullable
    protected String getService() throws ApiException {
        return getCurrentService();
    }

    @Nullable
    protected final String getCurrentService() {
        return mService;
    }

    protected final void setService(@Nullable final String service) {
        mService = service;
    }

    @Override
    protected void onPrepareSession(@NonNull final R request) throws ApiException {
        super.onPrepareSession(request);
        request.guid = this.getActiveGuid();

        // throw an exception if there isn't an active guid
        if(request.guid == null) {
            throw new InvalidSessionApiException();
        }
    }

    @Override
    protected boolean isSessionInvalid(@NonNull final HttpURLConnection conn, @NonNull final R request)
            throws IOException {
        // short-circuit if we already know the session is invalid
        if (super.isSessionInvalid(conn, request)) {
            return true;
        }

        // Check 401 Unauthorized responses to see if it's because of needing CAS authentication
        if (conn.getResponseCode() == HTTP_UNAUTHORIZED) {
            // Check the WWW-Authenticate challenge
            final String auth = conn.getHeaderField("WWW-Authenticate");
            if (auth != null) {
                // is this a CAS WWW-Authenticate challenge
                final HttpHeaderUtils.Challenge challenge = HttpHeaderUtils.parseChallenge(auth);
                if (challenge.scheme.equals("CAS")) {
                    // update service if one is returned
                    final String service = challenge.params.get("service");
                    if (service != null && service.length() > 0) {
                        this.setService(service);
                    }

                    // this was a CAS WWW-Authenticate, so the current session is invalid
                    return true;
                }
            }
        }

        // assume the session is still valid
        return false;
    }

    @Override
    protected void onCleanupRequest(@NonNull R request) {
        super.onCleanupRequest(request);
        request.guid = null;
    }

    public static class Session extends AbstractApi.Session {
        @Nullable
        private final String guid;

        protected Session(@Nullable final String id, @NonNull final String guid) {
            this(id, guid, PREF_SESSION_BASE_NAME);
        }

        protected Session(@Nullable final String id, @NonNull final String guid, @NonNull final String baseAttrName) {
            super(id, guid.toUpperCase(Locale.US) + "." + baseAttrName);
            this.guid = guid.toUpperCase(Locale.US);
        }

        protected Session(@NonNull final SharedPreferences prefs, @NonNull final String guid) {
            this(prefs, guid, PREF_SESSION_BASE_NAME);
        }

        protected Session(@NonNull final SharedPreferences prefs, @NonNull final String guid,
                          @NonNull final String baseAttrName) {
            super(prefs, guid.toUpperCase(Locale.US) + "." + baseAttrName);
            this.guid = guid.toUpperCase(Locale.US);;
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
            return super.equals(o) && !(guid != null ? !guid.equals(that.guid) : that.guid != null);
        }
    }

    public static class Request<S extends Session> extends AbstractApi.Request<S> {
        // session attributes
        public String guid = null;

        public Request(@NonNull final String path) {
            super(path);
            // use sessions by default for The Key protected APIs
            this.useSession = true;
        }
    }
}
