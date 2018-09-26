package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;

public interface Mapper<T> {
    @NonNull
    ContentValues toContentValues(@NonNull T obj, @NonNull String[] projection);

    @NonNull
    T toObject(@NonNull Cursor c);
}
