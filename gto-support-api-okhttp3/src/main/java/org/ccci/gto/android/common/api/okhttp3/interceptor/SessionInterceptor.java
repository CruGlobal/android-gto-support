package org.ccci.gto.android.common.api.okhttp3.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.api.Session;
import org.ccci.gto.android.common.api.okhttp3.EstablishSessionApiException;
import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class SessionInterceptor<S extends Session> implements Interceptor {
    private static final boolean DEFAULT_RETURN_INVALID_SESSION_RESPONSES = false;
    protected final Object mLockSession = new Object();

    @NonNull
    protected final Context mContext;
    private final boolean mReturnInvalidSessionResponses;
    @NonNull
    private final String mPrefFile;

    protected SessionInterceptor(@NonNull final Context context) {
        this(context, DEFAULT_RETURN_INVALID_SESSION_RESPONSES, null);
    }

    protected SessionInterceptor(@NonNull final Context context, final boolean returnInvalidSessionResponses) {
        this(context, returnInvalidSessionResponses, null);
    }

    protected SessionInterceptor(@NonNull final Context context, @Nullable final String prefFile) {
        this(context, DEFAULT_RETURN_INVALID_SESSION_RESPONSES, prefFile);
    }

    protected SessionInterceptor(@NonNull final Context context, final boolean returnInvalidSessionResponses,
                                 @Nullable final String prefFile) {
        mContext = context.getApplicationContext();
        mReturnInvalidSessionResponses = returnInvalidSessionResponses;
        mPrefFile = prefFile != null ? prefFile : getClass().getSimpleName();
    }

    @NonNull
    protected final SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE);
    }

    @Override
    public final Response intercept(@NonNull final Chain chain) throws IOException {
        // get the session, establish a session if one doesn't exist or if we have a stale session
        S session;
        synchronized (mLockSession) {
            session = loadSession();
            if (session == null) {
                try {
                    session = establishSession();
                } catch (final IOException e) {
                    // wrap establish session IOExceptions
                    throw new EstablishSessionApiException(e);
                }

                // save the newly established session
                if (session != null && session.isValid()) {
                    saveSession(session);
                }
            }
        }

        // throw an exception if we don't have a valid session
        if (session == null) {
            throw new InvalidSessionApiException();
        }

        // process request & response
        return processResponse(chain.proceed(attachSession(chain.request(), session)), session);
    }

    @NonNull
    protected abstract Request attachSession(@NonNull Request request, @NonNull S session);

    @CallSuper
    protected Response processResponse(@NonNull final Response response, @NonNull S session) throws IOException {
        // make sure the response is valid
        if (isSessionInvalid(response)) {
            // reset the session
            synchronized (mLockSession) {
                // only reset if this is still the same session
                final S active = loadSession();
                if (active != null && active.equals(session)) {
                    deleteSession(session);
                }
            }

            // throw an invalid session exception because our session was invalid
            if (!mReturnInvalidSessionResponses) {
                throw new InvalidSessionApiException();
            }
        }

        return response;
    }

    protected boolean isSessionInvalid(@NonNull final Response response) throws IOException {
        return false;
    }

    @Nullable
    private S loadSession() {
        // load a pre-existing session
        final SharedPreferences prefs = this.getPrefs();
        final S session;
        synchronized (mLockSession) {
            session = loadSession(prefs);
        }

        // only return valid sessions
        return session != null && session.isValid() ? session : null;
    }

    @Nullable
    protected abstract S loadSession(@NonNull SharedPreferences prefs);

    @Nullable
    protected S establishSession() throws IOException {
        return null;
    }

    protected final void saveSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = this.getPrefs().edit();
        session.save(prefs);

        synchronized (mLockSession) {
            prefs.apply();
        }
    }

    private void deleteSession(@NonNull final S session) {
        final SharedPreferences.Editor prefs = getPrefs().edit();
        session.delete(prefs);

        synchronized (mLockSession) {
            prefs.apply();
        }
    }
}
