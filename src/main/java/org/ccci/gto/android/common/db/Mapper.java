package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;

public interface Mapper<T> {
    ContentValues toContentValues(T obj);

    ContentValues toContentValues(T obj, String[] projection);

    T toObject(Cursor c);
}
