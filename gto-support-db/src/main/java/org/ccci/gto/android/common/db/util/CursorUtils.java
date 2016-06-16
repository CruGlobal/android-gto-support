package org.ccci.gto.android.common.db.util;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.util.LocaleCompat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

public final class CursorUtils {
    /**
     * returns a boolean value stored in the specified column. (SQLite doesn't
     * support booleans, so we fake it using an integer)
     *
     * @param c        SQLite results Cursor
     * @param field    the name of the column
     * @param defValue the default value
     * @return the boolean value
     */
    public static boolean getBool(@NonNull final Cursor c, @NonNull final String field, final boolean defValue) {
        final int index = c.getColumnIndex(field);
        if (index != -1) {
            final int value = c.getInt(index);
            if (value == 1) {
                return true;
            } else if (value == 0) {
                return false;
            }
        }
        return defValue;
    }

    public static double getDouble(@NonNull final Cursor c, @NonNull final String field) {
        return getDouble(c, field, 0);
    }

    public static double getDouble(@NonNull final Cursor c, @NonNull final String field, final double defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getDouble(index) : defValue;
    }

    public static int getInt(@NonNull final Cursor c, @NonNull final String field) {
        return getInt(c, field, 0);
    }

    public static int getInt(@NonNull final Cursor c, @NonNull final String field, final int defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getInt(index) : defValue;
    }

    public static long getLong(@NonNull final Cursor c, @NonNull final String field) {
        // if defValue is @NonNull, then getLong() will return @NonNull
        //noinspection ConstantConditions
        return getLong(c, field, 0L);
    }

    /**
     * @param c        The Cursor we are fetching the value from
     * @param field    The column we are requesting the value of
     * @param defValue The default value to return when the column doesn't exist, is invalid, or is null
     * @return the value for the specified column in the current row of the specified Cursor. Or the default value
     * if the column is invalid, null or non-existant
     */
    @Nullable
    public static Long getLong(@NonNull final Cursor c, @NonNull final String field, @Nullable final Long defValue) {
        final int index = c.getColumnIndex(field);
        if (index != -1 && !c.isNull(index)) {
            try {
                return Long.parseLong(c.getString(index));
            } catch (final NumberFormatException ignored) {
            }
        }

        return defValue;
    }

    @Nullable
    public static String getString(@NonNull final Cursor c, @NonNull final String field) {
        return getString(c, field, null);
    }

    /**
     * @param c        The Cursor we are fetching the value from
     * @param column   The column we are requesting the value of
     * @param defValue The default value to return when the column doesn't exist or is null
     * @return the value for the specified column in the current row of the specified Cursor. Or the default value
     * if the column is null or non-existant
     */
    @Nullable
    public static String getString(@NonNull final Cursor c, @NonNull final String column,
                                   @Nullable final String defValue) {
        final int index = c.getColumnIndex(column);
        final String value = index != -1 ? c.getString(index) : null;
        return value != null ? value : defValue;
    }

    @NonNull
    public static String getNonNullString(@NonNull final Cursor c, @NonNull final String field,
                                          @NonNull final String defValue) {
        // if defValue is @NonNull, then getString() will return @NonNull
        //noinspection ConstantConditions
        return getString(c, field, defValue);
    }

    @Nullable
    public static BigDecimal getBigDecimal(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final BigDecimal defValue) {
        final String raw = getString(c, field, null);
        if (raw != null) {
            try {
                return new BigDecimal(raw);
            } catch (final Exception ignored) {
            }
        }
        return defValue;
    }

    @Nullable
    public static Date getDate(@NonNull final Cursor c, @NonNull final String field) {
        return getDate(c, field, null);
    }

    @Nullable
    public static Date getDate(@NonNull final Cursor c, @NonNull final String field, @Nullable final Date defValue) {
        final String raw = getString(c, field, null);
        if (raw != null) {
            try {
                return new Date(Long.parseLong(raw));
            } catch (final Exception ignored) {
            }
        }
        return defValue;
    }

    @Nullable
    public static <E extends Enum<E>> E getEnum(@NonNull final Cursor c, @NonNull final String field,
                                                final Class<E> clazz) {
        return getEnum(c, field, clazz, null);
    }

    @Nullable
    public static <E extends Enum<E>> E getEnum(@NonNull final Cursor c, @NonNull final String field,
                                                final Class<E> clazz, @Nullable final E defValue) {
        final String raw = getString(c, field, defValue != null ? defValue.toString() : null);

        if (raw != null) {
            try {
                return Enum.valueOf(clazz, raw);
            } catch (final Exception ignored) {
            }
        }

        return defValue;
    }

    @NonNull
    @SuppressWarnings("ConstantConditions")
    public static <E extends Enum<E>> E getNonNullEnum(@NonNull final Cursor c, @NonNull final String field,
                                                       final Class<E> clazz, @NonNull final E defValue) {
        return getEnum(c, field, clazz, defValue);
    }

    @Nullable
    public static Locale getLocale(@NonNull final Cursor c, @NonNull final String field) {
        return getLocale(c, field, null);
    }

    @Nullable
    public static Locale getLocale(@NonNull final Cursor c, @NonNull final String field,
                                   @Nullable final Locale defValue) {
        final String raw = getString(c, field, null);
        if (raw != null) {
            try {
                return LocaleCompat.forLanguageTag(raw);
            } catch (final Exception ignored) {
            }
        }
        return defValue;
    }

    @NonNull
    public static Locale getNonNullLocale(@NonNull final Cursor c, @NonNull final String field,
                                          @NonNull final Locale defValue) {
        final Locale val = getLocale(c, field, defValue);
        return val != null ? val : defValue;
    }
}
