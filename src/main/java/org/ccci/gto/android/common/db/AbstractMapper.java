package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.util.CursorUtils;

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

    protected final int getInt(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getInt(c, field);
    }

    protected final int getInt(@NonNull final Cursor c, @NonNull final String field, final int defValue) {
        return CursorUtils.getInt(c, field, defValue);
    }

    protected final long getLong(@NonNull final Cursor c, @NonNull final String field) {
        return CursorUtils.getLong(c, field);
    }

    protected final long getLong(@NonNull final Cursor c, @NonNull final String field, final long defValue) {
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
