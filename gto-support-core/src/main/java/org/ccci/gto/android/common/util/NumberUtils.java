package org.ccci.gto.android.common.util;

import android.support.annotation.Nullable;

public class NumberUtils {
    @Nullable
    public static Integer toInteger(@Nullable final String raw, @Nullable final Integer defaultValue) {
        if (raw != null) {
            try {
                return Integer.valueOf(raw);
            } catch (final NumberFormatException ignored) {
            }
        }

        return defaultValue;
    }
}
