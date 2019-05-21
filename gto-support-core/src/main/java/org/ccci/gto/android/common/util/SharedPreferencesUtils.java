package org.ccci.gto.android.common.util;

import android.content.SharedPreferences;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @deprecated Since v3.0.0, there are official alternatives for all of this logic
 */
@Deprecated
public class SharedPreferencesUtils {
    /**
     * @deprecated Since v3.0.0, use {@link SharedPreferences.Editor#putStringSet(String, Set)} directly instead.
     */
    @Deprecated
    public static SharedPreferences.Editor putStringSet(@NonNull final SharedPreferences.Editor prefs,
                                                        @NonNull final String key, @Nullable final Set<String> values) {
        return prefs.putStringSet(key, values);
    }

    /**
     * @deprecated Since v3.0.0, use {@link SharedPreferences.Editor#getStringSet(String, Set)} directly instead.
     */
    @Deprecated
    public static Set<String> getStringSet(@NonNull final SharedPreferences prefs, @NonNull final String key,
                                           @Nullable final Set<String> defValue) {
        return prefs.getStringSet(key, defValue);
    }
}
