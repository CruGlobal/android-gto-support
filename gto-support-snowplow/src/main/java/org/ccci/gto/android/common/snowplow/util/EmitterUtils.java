package org.ccci.gto.android.common.snowplow.util;

import com.snowplowanalytics.snowplow.tracker.Emitter;

import org.ccci.gto.android.common.okhttp3.util.OkHttpClientUtil;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import timber.log.Timber;

public final class EmitterUtils {
    public static void attachOkHttp3GlobalInterceptors(@NonNull final Emitter emitter) {
        try {
            final Field field = Emitter.class.getDeclaredField("client");
            field.setAccessible(true);
            final OkHttpClient original = (OkHttpClient) field.get(emitter);
            field.set(emitter, OkHttpClientUtil.attachGlobalInterceptors(original.newBuilder()).build());
        } catch (final Exception e) {
            Timber.tag("EmitterUtils")
                    .e(e, "error attaching OkHttp global interceptors to SnowPlow Emitter");
        }
    }
}
