package org.ccci.gto.android.common.okhttp3.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class OkHttpClientUtil {
    private static final List<Interceptor> GLOBAL_NETWORK_INTERCEPTORS = new ArrayList<>();

    public static void addGlobalNetworkInterceptor(@NonNull final Interceptor interceptor) {
        GLOBAL_NETWORK_INTERCEPTORS.add(interceptor);
    }

    @NonNull
    public static OkHttpClient.Builder attachGlobalInterceptors(@NonNull final OkHttpClient.Builder builder) {
        for (final Interceptor interceptor : GLOBAL_NETWORK_INTERCEPTORS) {
            builder.addNetworkInterceptor(interceptor);
        }
        return builder;
    }
}
