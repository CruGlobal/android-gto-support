package org.ccci.gto.android.common.db.util;

import android.database.Cursor;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    /**
     * @deprecated Since v3.3.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getDouble(Cursor, String)} instead.
     */
    @Deprecated
    public static double getDouble(@NonNull final Cursor c, @NonNull final String field) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getDouble(c, field, 0d);
    }

    /**
     * @deprecated Since v3.3.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getDouble(Cursor, String)} instead.
     */
    @Deprecated
    public static double getDouble(@NonNull final Cursor c, @NonNull final String field, final double defValue) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getDouble(c, field, defValue);
    }

    /**
     * @deprecated Since v3.0.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getLong(Cursor, String, Long)} instead.
     */
    @Deprecated
    public static long getLong(@NonNull final Cursor c, @NonNull final String field) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getNonNullLong(c, field, 0L);
    }

    /**
     * @deprecated Since v3.0.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getLong(Cursor, String, Long)} instead.
     */
    @Nullable
    @Deprecated
    @Contract("_, _, !null -> !null")
    public static Long getLong(@NonNull final Cursor c, @NonNull final String field, @Nullable final Long defValue) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getLong(c, field, defValue);
    }

    /**
     * @deprecated Since v3.0.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getString(Cursor, String)} instead.
     */
    @Nullable
    @Deprecated
    public static String getString(@NonNull final Cursor c, @NonNull final String field) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
    }

    /**
     * @deprecated Since v3.0.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getString(Cursor, String, String)} instead.
     */
    @Nullable
    @Deprecated
    @Contract("_, _, !null -> !null")
    public static String getString(@NonNull final Cursor c, @NonNull final String column,
                                   @Nullable final String defValue) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getString(c, column, defValue);
    }

    @Nullable
    public static BigDecimal getBigDecimal(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final BigDecimal defValue) {
        final String raw = org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
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
        final String raw = org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
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
                                                @NonNull final Class<E> clazz) {
        return getEnum(c, field, clazz, null);
    }

    @Nullable
    @Contract("_, _, _, !null -> !null")
    public static <E extends Enum<E>> E getEnum(@NonNull final Cursor c, @NonNull final String field,
                                                @NonNull final Class<E> clazz, @Nullable final E defValue) {
        final String raw = org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
        if (raw != null) {
            try {
                return Enum.valueOf(clazz, raw);
            } catch (final Exception ignored) {
            }
        }

        return defValue;
    }

    @Nullable
    public static JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field) {
        return getJSONArray(c, field, null);
    }

    @Nullable
    public static JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field,
                                         @Nullable final JSONArray defValue) {
        final String raw = org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
        if (raw != null) {
            try {
                return new JSONArray(raw);
            } catch (final Exception ignored) {
            }
        }
        return defValue;
    }

    @Nullable
    public static JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field) {
        return getJSONObject(c, field, null);
    }

    @Nullable
    public static JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final JSONObject defValue) {
        final String raw = org.ccci.gto.android.common.util.database.CursorUtils.getString(c, field);
        if (raw != null) {
            try {
                return new JSONObject(raw);
            } catch (final Exception ignored) {
            }
        }
        return defValue;
    }

    /**
     * @deprecated Since v3.4.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getLocale(Cursor, String)} instead.
     */
    @Nullable
    @Deprecated
    public static Locale getLocale(@NonNull final Cursor c, @NonNull final String field) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getLocale(c, field);
    }

    /**
     * @deprecated Since v3.4.0,
     * use {@link org.ccci.gto.android.common.util.database.CursorUtils#getLocale(Cursor, String, Locale)} instead.
     */
    @Nullable
    @Deprecated
    @Contract("_, _, !null -> !null")
    public static Locale getLocale(@NonNull final Cursor c, @NonNull final String field,
                                   @Nullable final Locale defValue) {
        return org.ccci.gto.android.common.util.database.CursorUtils.getLocale(c, field, defValue);
    }
}
