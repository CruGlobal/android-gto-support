package org.ccci.gto.android.common.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public abstract class WalSQLiteOpenHelper extends SQLiteOpenHelper {
    public WalSQLiteOpenHelper(@NonNull final Context context, final String name,
                               final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
        initWal();
    }

    public WalSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                               DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        initWal();
    }

    private void initWal() {
        setWriteAheadLoggingEnabled(true);
    }
}
