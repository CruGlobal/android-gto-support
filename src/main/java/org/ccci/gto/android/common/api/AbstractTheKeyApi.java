package org.ccci.gto.android.common.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import me.thekey.android.TheKey;

public abstract class AbstractTheKeyApi<R extends AbstractTheKeyApi.Request<S>, S extends AbstractTheKeyApi.Session>
        extends AbstractApi<R, S> {
    protected final TheKey mTheKey;
    protected final String mGuid;

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
    protected void onCleanupRequest(@NonNull R request) {
        super.onCleanupRequest(request);
        request.guid = null;
    }

    protected static class Session extends AbstractApi.Session {
        @Nullable
        private final String guid;

        protected Session(@Nullable final String id, @NonNull final String guid) {
            this(id, guid, PREF_SESSION_BASE_NAME);
        }

        protected Session(@Nullable final String id, @NonNull final String guid, @NonNull final String baseAttrName) {
            super(id, guid.toUpperCase(Locale.US) + "." + baseAttrName);
            this.guid = guid;
        }

        protected Session(@NonNull final SharedPreferences prefs, @NonNull final String guid) {
            this(prefs, guid, PREF_SESSION_BASE_NAME);
        }

        protected Session(@NonNull final SharedPreferences prefs, @NonNull final String guid,
                          @NonNull final String baseAttrName) {
            super(prefs, guid.toUpperCase(Locale.US) + "." + baseAttrName);
            this.guid = guid;
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

    protected static class Request<S extends Session> extends AbstractApi.Request<S> {
        // session attributes
        public String guid = null;

        public Request(@NonNull final String path) {
            super(path);
            // use sessions by default for The Key protected APIs
            this.useSession = true;
        }
    }
}
