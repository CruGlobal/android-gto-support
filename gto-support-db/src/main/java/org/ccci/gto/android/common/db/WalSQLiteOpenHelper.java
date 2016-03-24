package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;

public abstract class WalSQLiteOpenHelper extends SQLiteOpenHelperCompat {
    public WalSQLiteOpenHelper(@NonNull final Context context, final String name,
                               final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
        initWal();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WalSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                               DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        initWal();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initWal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setWriteAheadLoggingEnabled(true);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onConfigure(@NonNull final SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // enable WAL now since we couldn't enable it when creating the database
            db.enableWriteAheadLogging();
        }
    }
}
