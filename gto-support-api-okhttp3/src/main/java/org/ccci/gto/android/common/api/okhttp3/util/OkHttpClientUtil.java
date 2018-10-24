package org.ccci.gto.android.common.api.okhttp3.util;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @see org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil
 * @deprecated Since v1.3.2, use {@link org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil} instead.
 */
@Deprecated
public class OkHttpClientUtil {
    /**
     * @see org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil#addGlobalNetworkInterceptor(Interceptor)
     * @deprecated Since v1.3.2, use
     * {@link org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil#addGlobalNetworkInterceptor(Interceptor)}
     * directly.
     */
    @Deprecated
    public static void addGlobalNetworkInterceptor(@NonNull final Interceptor interceptor) {
        org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil.addGlobalNetworkInterceptor(interceptor);
    }

    /**
     * @see org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil#attachGlobalInterceptors(OkHttpClient.Builder)
     * @deprecated Since v1.3.2, use
     * {@link org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil#attachGlobalInterceptors(OkHttpClient.Builder)}
     * directly.
     */
    @Deprecated
    @NonNull
    public static OkHttpClient.Builder attachGlobalInterceptors(@NonNull final OkHttpClient.Builder builder) {
        return org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil.attachGlobalInterceptors(builder);
    }
}
