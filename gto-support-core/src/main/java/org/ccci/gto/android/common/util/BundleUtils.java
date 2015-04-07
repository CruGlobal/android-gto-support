package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;

public class BundleUtils {
    @Nullable
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <T extends Parcelable> T[] getParcelableArray(@NonNull final Bundle bundle,
                                                                @Nullable final String key,
                                                                @NonNull final Class<T> clazz) {
        final Parcelable[] raw = bundle.getParcelableArray(key);
        if (raw == null) {
            return null;
        }

        // copy all objects to typed array
        final T[] arr = (T[]) Array.newInstance(clazz, raw.length);
        System.arraycopy(raw, 0, arr, 0, raw.length);
        return arr;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static String getString(@NonNull final Bundle bundle, @NonNull final String key,
                                   @Nullable final String defaultValue) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            final String value = bundle.getString(key);
            return value != null ? value : defaultValue;
        } else {
            return bundle.getString(key, defaultValue);
        }
    }
}
