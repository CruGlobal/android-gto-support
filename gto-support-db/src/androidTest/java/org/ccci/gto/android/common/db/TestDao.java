package org.ccci.gto.android.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.db.model.Root;

import static org.ccci.gto.android.common.db.Expression.field;

public class TestDao extends AbstractDao {
    TestDao(@NonNull final Context context) {
        super(new TestDatabase(context));
        registerType(Root.class, TestContract.RootTable.TABLE_NAME,
                     TestContract.RootTable.PROJECTION_ALL, null, null);
    }

    static class TestContract extends BaseContract {
        static class RootTable implements Base {
            static final String TABLE_NAME = "root";
            static final Table<Root> TABLE = Table.forClass(Root.class);

            static final String COLUMN_ID = _ID;
            static final String COLUMN_TEST = "test";

            static final String[] PROJECTION_ALL = {COLUMN_ID, COLUMN_TEST};

            static final String SQL_COLUMN_ID = COLUMN_ID + " INTEGER PRIMARY KEY";
            static final String SQL_COLUMN_TEST = COLUMN_TEST + " TEXT";

            static final Field FIELD_TEST = field(TABLE, COLUMN_TEST);

            static final String SQL_CREATE_TABLE = create(TABLE_NAME, SQL_COLUMN_ID, SQL_COLUMN_TEST);
        }
    }

    public static class TestDatabase extends WalSQLiteOpenHelper {
        TestDatabase(@NonNull final Context context) {
            super(context, "test_db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TestContract.RootTable.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new IllegalStateException("onUpgrade should no be triggered");
        }
    }
}
