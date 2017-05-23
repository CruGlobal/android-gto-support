package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.db.util.CursorUtils;
import org.ccci.gto.android.common.util.LocaleCompat;
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

    protected final double getDouble(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getDouble(c, field);
    }

    protected final double getDouble(@NonNull final Cursor c, @NonNull final String field, final double defValue) {
        return CursorUtils.getDouble(c, field, defValue);
    }

    protected final int getInt(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getInt(c, field);
    }

    protected final int getInt(@NonNull final Cursor c, @NonNull final String field, final int defValue) {
        return CursorUtils.getInt(c, field, defValue);
    }

    protected final long getLong(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getLong(c, field);
    }

    @Nullable
    protected final Long getLong(@NonNull final Cursor c, @NonNull final String field, @Nullable final Long defValue) {
        return CursorUtils.getLong(c, field, defValue);
    }

    @Nullable
    protected final String getString(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getString(c, field);
    }

    @Nullable
    protected final String getString(@NonNull final Cursor c, @NonNull final String field,
                                     @Nullable final String defValue) {
        return CursorUtils.getString(c, field, defValue);
    }

    /**
     * @deprecated Since 1.1.1, use {@link AbstractMapper#getString(Cursor, String, String)} instead now that there is
     * an IntelliJ Contract defined for it.
     */
    @NonNull
    @Deprecated
    protected final String getNonNullString(@NonNull final Cursor c, @NonNull final String field,
                                            @NonNull final String defValue) {
        return CursorUtils.getString(c, field, defValue);
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
    protected final JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getJSONArray(c, field);
    }

    @Nullable
    protected final JSONArray getJSONArray(@NonNull final Cursor c, @NonNull final String field,
                                           @Nullable final JSONArray defValue) {
        return CursorUtils.getJSONArray(c, field, defValue);
    }

    @Nullable
    protected final JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getJSONObject(c, field);
    }

    @Nullable
    protected final JSONObject getJSONObject(@NonNull final Cursor c, @NonNull final String field,
                                             @Nullable final JSONObject defValue) {
        return CursorUtils.getJSONObject(c, field, defValue);
    }

    @Nullable
    protected final Locale getLocale(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getLocale(c, field);
    }

    @Nullable
    protected final Locale getLocale(@NonNull final Cursor c, @NonNull final String field,
                                     @Nullable final Locale defValue) {
        return CursorUtils.getLocale(c, field, defValue);
    }

    /**
     * @deprecated Since 1.1.1, use {@link AbstractMapper#getLocale(Cursor, String, Locale)} instead now that there is
     * an IntelliJ Contract defined for it.
     */
    @NonNull
    @Deprecated
    protected final Locale getNonNullLocale(@NonNull final Cursor c, @NonNull final String field,
                                            @NonNull final Locale defValue) {
        return CursorUtils.getLocale(c, field, defValue);
    }

    @Nullable
    protected final Long serialize(@Nullable final Date date) {
        return date != null ? date.getTime() : null;
    }

    @Nullable
    protected final String serialize(@Nullable final Locale locale) {
        return locale != null ? LocaleCompat.toLanguageTag(locale) : null;
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
    protected abstract T newObject(@NonNull final Cursor c);

    @NonNull
    @Override
    public T toObject(@NonNull final Cursor c) {
        return this.newObject(c);
    }
}
