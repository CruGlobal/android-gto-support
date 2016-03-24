package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public abstract class SQLiteOpenHelperCompat extends SQLiteOpenHelper {
    public SQLiteOpenHelperCompat(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SQLiteOpenHelperCompat(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                                  DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        // call super onConfigure only if it would exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.onConfigure(db);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // trigger onConfigure now if it isn't triggered by Android itself
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            onConfigure(db);
        }

        // trigger parent onOpen
        super.onOpen(db);
    }
}
