package org.ccci.gto.android.common.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @deprecated Since v1.0.2, use {@link Bundle} directly.
 */
@Deprecated
public class BundleCompat {
    /**
     * @deprecated Since v1.0.2, use {@link Bundle#getString(String, String)} directly.
     */
    @Deprecated
    public static String getString(@NonNull final Bundle bundle, @NonNull final String key,
                                   @Nullable final String defaultValue) {
        return bundle.getString(key, defaultValue);
    }
}
