package org.ccci.gto.android.common.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @deprecated Since v3.0.0, This class is no longer necessary, use {@link SQLiteOpenHelper} directly.
 */
@Deprecated
public abstract class SQLiteOpenHelperCompat extends SQLiteOpenHelper {
    public SQLiteOpenHelperCompat(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteOpenHelperCompat(Context context, String name, CursorFactory factory, int version,
                                  DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
}
