package org.ccci.gto.android.common.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @deprecated Since 1.0.2, use {@link org.ccci.gto.android.common.compat.os.BundleCompat} instead.
 */
@Deprecated
public class BundleCompat {
    /**
     * @deprecated Since 1.0.2, use {@link org.ccci.gto.android.common.compat.os.BundleCompat#getString(Bundle, String, String)} instead.
     */
    @Deprecated
    public static String getString(@NonNull final Bundle bundle, @NonNull final String key,
                                   @Nullable final String defaultValue) {
        return org.ccci.gto.android.common.compat.os.BundleCompat.getString(bundle, key, defaultValue);
    }
}
