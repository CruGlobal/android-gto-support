package org.ccci.gto.android.common.picasso.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TintContextWrapper;
import timber.log.Timber;

public final class PicassoUtils {
    @NonNull
    @SuppressLint("RestrictedApi")
    public Picasso.Builder injectVectorAwareContext(@NonNull final Context context,
                                                    @NonNull final Picasso.Builder builder) {
        // XXX: forcibly inject a TintContextWrapper context to support loading Support VectorDrawables
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Field f = Picasso.Builder.class.getDeclaredField("context");
                f.setAccessible(true);
                f.set(builder, TintContextWrapper.wrap(context.getApplicationContext()));
            } catch (final Exception e) {
                Timber.tag(PicassoUtils.class.getSimpleName())
                        .e(e, "Error injecting a vector aware context object into Picasso");
            }
        }

        return builder;
    }
}
