package org.ccci.gto.android.common.util;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * @deprecated Since v0.9.0, this class currently doesn't provide any necessary backwards compatible functionality
 */
@Deprecated
public class SharedPreferencesCompat {
    /**
     * @deprecated Since v0.9.0, use {@link SharedPreferences.Editor#apply()} directly.
     */
    @Deprecated
    public static void apply(@NonNull final SharedPreferences.Editor prefs) {
        prefs.apply();
    }
}
