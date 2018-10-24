package org.ccci.gto.android.common.util;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

public class NumberUtils {
    @Nullable
    @Contract("_, !null -> !null")
    public static Integer toInteger(@Nullable final String raw, @Nullable final Integer defaultValue) {
        if (raw != null) {
            try {
                return Integer.valueOf(raw);
            } catch (final NumberFormatException ignored) {
            }
        }

        return defaultValue;
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Long toLong(@Nullable final String raw, @Nullable final Long defaultValue) {
        if (raw != null) {
            try {
                return Long.valueOf(raw);
            } catch (final NumberFormatException ignored) {
            }
        }

        return defaultValue;
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Double toDouble(@Nullable final String raw, @Nullable final Double defaultValue) {
        if (raw != null) {
            try {
                return Double.valueOf(raw);
            } catch (final NumberFormatException ignored) {
            }
        }

        return defaultValue;
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Float toFloat(@Nullable final String raw, @Nullable final Float defaultValue) {
        if (raw != null) {
            try {
                return Float.valueOf(raw);
            } catch (final NumberFormatException ignored) {
            }
        }

        return defaultValue;
    }
}
