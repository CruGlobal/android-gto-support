package org.ccci.gto.android.common.util;

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

    public static void putEnum(@NonNull final Bundle bundle, @Nullable final String key,
                               @Nullable final Enum<?> value) {
        bundle.putString(key, value != null ? value.name() : null);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key) {
        return getEnum(bundle, type, key, null);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key, @Nullable final T defValue) {
        final String raw = bundle.getString(key);
        if (raw == null) {
            return defValue;
        }

        try {
            return Enum.valueOf(type, raw);
        } catch (final IllegalArgumentException e) {
            return defValue;
        }
    }
}
