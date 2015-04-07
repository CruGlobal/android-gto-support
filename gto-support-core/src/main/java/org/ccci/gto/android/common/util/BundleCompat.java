package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BundleCompat {
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
