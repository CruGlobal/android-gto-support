package org.ccci.gto.android.common.db.util;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.util.database.CursorKt;
import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

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
    public static JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field,
                                         @Nullable final JSONArray defValue) {
        final JSONArray json = CursorKt.getJSONArrayOrNull(c, field);
        return json != null ? json : defValue;
    }

    /**
     * @deprecated Since v3.6.2, use {@link CursorKt#getJSONObjectOrNull(Cursor, String)} instead.
     */
    @Nullable
    @Deprecated
    public static JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field) {
        return CursorKt.getJSONObjectOrNull(c, field);
    }

    @Nullable
    public static JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final JSONObject defValue) {
        final JSONObject json = CursorKt.getJSONObjectOrNull(c, field);
        return json != null ? json : defValue;
    }
}
