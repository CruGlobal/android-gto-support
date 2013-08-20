package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;

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
    protected final boolean getBool(final Cursor c, final String field, final boolean defValue) {
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

    protected final int getInt(final Cursor c, final String field) {
        return this.getInt(c, field, 0);
    }

    protected final int getInt(final Cursor c, final String field, final int defValue) {
        return CursorUtils.getInt(c,field,defValue);
    }

    protected final long getLong(final Cursor c, final String field) {
        return CursorUtils.getLong(c,field);
    }

    protected final long getLong(final Cursor c, final String field, final long defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getLong(index) : defValue;
    }

    protected final String getString(final Cursor c, final String field) {
        return this.getString(c, field, null);
    }

    protected final String getString(final Cursor c, final String field, final String defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getString(index) : defValue;
    }

    @Override
    public final ContentValues toContentValues(final T obj, final String[] projection) {
        // only add values in the projection
        final ContentValues values = new ContentValues();
        for (final String field : projection) {
            this.mapField(values, field, obj);
        }
        return values;
    }

    protected void mapField(final ContentValues values, final String field, final T obj) {
        // ignore unrecognized fields
    }

    protected abstract T newObject(final Cursor c);

    @Override
    public T toObject(final Cursor c) {
        return this.newObject(c);
    }
}
