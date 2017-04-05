package org.ccci.gto.android.common.api.okhttp3.interceptor;

import android.support.annotation.NonNull;

import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public final class SessionRetryInterceptor implements Interceptor {
    private static final int ATTEMPTS_DEFAULT = 3;
    private static final int ATTEMPTS_MIN = 1;
    private static final int ATTEMPTS_MAX = 20;

    private final int mAttempts;

    public SessionRetryInterceptor() {
        this(ATTEMPTS_DEFAULT);
    }

    public SessionRetryInterceptor(final int attempts) {
        mAttempts = Math.max(Math.min(attempts, ATTEMPTS_MAX), ATTEMPTS_MIN);
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        int tries = 0;
        while (true) {
            try {
                return chain.proceed(chain.request());
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
