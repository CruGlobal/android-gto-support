package org.ccci.gto.android.common.db;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public abstract class BaseContract {
    public interface Base extends BaseColumns {
        String COLUMN_ROWID = _ID;
        String SQL_COLUMN_ROWID = COLUMN_ROWID + " INTEGER PRIMARY KEY";
    }

    @NonNull
    public static String create(@NonNull final String table, @NonNull final String... sqlColumns) {
        return "CREATE TABLE " + table + " (" + TextUtils.join(",", sqlColumns) + ")";
    }

    @NonNull
    public static String drop(@NonNull final String table) {
        return "DROP TABLE IF EXISTS " + table;
    }
}
