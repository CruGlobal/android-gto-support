package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.db.util.CursorUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

public abstract class AbstractMapper<T> implements Mapper<T> {
    /**
     * returns a boolean value stored in the specified column. (SQLite doesn't
     * support booleans, so we fake it using an integer)
     *
     * @param c        SQLite results Cursor
     * @param field    the name of the column
     * @param defValue the default value
     * @return
     */
    protected final boolean getBool(@NonNull final Cursor c, @NonNull final String field, final boolean defValue) {
        return CursorUtils.getBool(c, field, defValue);
    }

    @Nullable
    protected final BigDecimal getBigDecimal(@NonNull final Cursor c, @NonNull final String field,
                                             @Nullable final BigDecimal defValue) {
        return CursorUtils.getBigDecimal(c, field, defValue);
    }

    @Nullable
    protected final Date getDate(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getDate(c, field);
    }

    @Nullable
    protected final Date getDate(@NonNull final Cursor c, @NonNull final String field, @Nullable final Date defValue) {
        return CursorUtils.getDate(c, field, defValue);
    }

    @Nullable
    protected final <E extends Enum<E>> E getEnum(@NonNull final Cursor c, @NonNull final String field,
                                                  @NonNull final Class<E> clazz) {
        return CursorUtils.getEnum(c, field, clazz);
    }

    @Nullable
    protected final <E extends Enum<E>> E getEnum(@NonNull final Cursor c, @NonNull final String field,
                                                  @NonNull final Class<E> clazz, @Nullable final E defValue) {
        return CursorUtils.getEnum(c, field, clazz, defValue);
    }

    @Nullable
    protected final JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final JSONArray defValue) {
        return CursorUtils.getJSONArray(c, field, defValue);
    }

    @Nullable
    protected final JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field,
                                             @Nullable final JSONObject defValue) {
        return CursorUtils.getJSONObject(c, field, defValue);
    }

    @Nullable
    protected final Long serialize(@Nullable final Date date) {
        return date != null ? date.getTime() : null;
    }

    @Nullable
    protected final String serialize(@Nullable final Locale locale) {
        return locale != null ? locale.toLanguageTag() : null;
    }

    /**
     * generic obj.toString() serializer. Known to work for Enums, JSONObjects, and JSONArrays.
     *
     * @param o the Object to serialize
     * @return representation of the Object
     */
    @Nullable
    protected final String serialize(@Nullable final Object o) {
        return o != null ? o.toString() : null;
    }

    @NonNull
    @Override
    public final ContentValues toContentValues(@NonNull final T obj, @NonNull final String[] projection) {
        // only add values in the projection
        final ContentValues values = new ContentValues();
        for (final String field : projection) {
            this.mapField(values, field, obj);
        }
        return values;
    }

    protected void mapField(@NonNull final ContentValues values, @NonNull final String field, @NonNull final T obj) {
        // ignore unrecognized fields
    }

    @NonNull
    protected abstract T newObject(@NonNull Cursor c);

    @NonNull
    @Override
    public T toObject(@NonNull final Cursor c) {
        return this.newObject(c);
    }
}
