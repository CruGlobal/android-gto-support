package org.ccci.gto.android.common.api.okhttp3.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public final class SessionRetryInterceptor implements Interceptor {
    private static final int ATTEMPTS_DEFAULT = 3;
    private static final int ATTEMPTS_MIN = 1;
    private static final int ATTEMPTS_MAX = 20;

    @Nullable
    private final SessionInterceptor mSessionInterceptor;
    private final int mAttempts;

    public SessionRetryInterceptor() {
        this(null, ATTEMPTS_DEFAULT);
    }

    public SessionRetryInterceptor(final int attempts) {
        this(null, attempts);
    }

    public SessionRetryInterceptor(@Nullable final SessionInterceptor sessionInterceptor, final int attempts) {
        mSessionInterceptor = sessionInterceptor;
        mAttempts = Math.max(Math.min(attempts, ATTEMPTS_MAX), ATTEMPTS_MIN);
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        int tries = 0;
        while (true) {
            try {
                final Response response = chain.proceed(chain.request());

                // retry request if the response indicates the session is invalid
                if (tries < mAttempts && mSessionInterceptor != null &&
                        mSessionInterceptor.isSessionInvalid(response)) {
                    continue;
                }

                return response;
            } catch (@NonNull final InvalidSessionApiException e) {
                tries++;
                if (tries < mAttempts) {
                    continue;
                }
                throw e;
            }
        }
    }
}
