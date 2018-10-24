package org.ccci.gto.android.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import org.ccci.gto.android.common.db.CommonTables.LastSyncTable;
import org.ccci.gto.android.common.db.Contract.CompoundTable;
import org.ccci.gto.android.common.db.Contract.RootTable;

class TestDatabase extends WalSQLiteOpenHelper {
    private TestDatabase(@NonNull final Context context) {
        super(context, "test_db", null, 1);
        resetDatabase(getWritableDatabase());
    }

    private static TestDatabase sInstance;
    static TestDatabase getInstance(@NonNull final Context context) {
        synchronized (TestDatabase.class) {
            if (sInstance == null) {
                sInstance = new TestDatabase(context.getApplicationContext());
            }
            return sInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RootTable.SQL_CREATE_TABLE);
        db.execSQL(CompoundTable.SQL_CREATE_TABLE);
        db.execSQL(LastSyncTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("onUpgrade should not be triggered");
    }

    private void resetDatabase(final SQLiteDatabase db) {
        try {
            db.beginTransaction();

            db.execSQL(RootTable.SQL_DELETE_TABLE);
            db.execSQL(CompoundTable.SQL_DELETE_TABLE);
            db.execSQL(LastSyncTable.SQL_DELETE_TABLE);

            onCreate(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
