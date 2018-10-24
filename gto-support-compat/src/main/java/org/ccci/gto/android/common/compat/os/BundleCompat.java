package org.ccci.gto.android.common.compat.os;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @deprecated Since 1.2.0, use {@link Bundle} directly.
 */
@Deprecated
public class BundleCompat {
    /**
     * @deprecated Since 1.2.0, use {@link Bundle#getString(String, String)} directly.
     */
    @Deprecated
    public static String getString(@NonNull final Bundle bundle, @NonNull final String key,
                                   @Nullable final String defaultValue) {
        return bundle.getString(key, defaultValue);
    }
}
