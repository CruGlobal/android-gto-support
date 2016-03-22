package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.db.util.CursorUtils;

import static org.ccci.gto.android.common.db.Expression.bind;
import static org.ccci.gto.android.common.db.Expression.field;

public class TestDao extends AbstractDao {
    TestDao(@NonNull final Context context) {
        super(new TestDatabase(context));
        registerType(Root.class, TestContract.RootTable.TABLE_NAME,
                     TestContract.RootTable.PROJECTION_ALL, new RootMapper(),
                     TestContract.RootTable.SQL_WHERE_PRIMARY_KEY);
    }

    @NonNull
    @Override
    protected Expression getPrimaryKeyWhere(@NonNull final Object obj) {
        if(obj instanceof Root) {
            return getPrimaryKeyWhere(Root.class, ((Root) obj).id);
        }
        return super.getPrimaryKeyWhere(obj);
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

            static final Field FIELD_ID = field(TABLE, COLUMN_ID);
            static final Field FIELD_TEST = field(TABLE, COLUMN_TEST);

            static final Expression SQL_WHERE_PRIMARY_KEY = FIELD_ID.eq(bind());
            static final Expression SQL_WHERE_ANY = FIELD_ID.ne(-1);

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

    public class RootMapper extends AbstractMapper<Root> {

        @Override
        protected void mapField(@NonNull ContentValues values, @NonNull String field, @NonNull Root obj) {
            switch(field) {
                case TestContract.RootTable.COLUMN_ID:
                    values.put(field, obj.id);
                    break;
                case TestContract.RootTable.COLUMN_TEST:
                    values.put(field, obj.test);
                    break;
                default:
                    super.mapField(values, field, obj);
                    break;
            }
        }

        @NonNull
        @Override
        protected Root newObject(@NonNull Cursor c) {
            return new Root();
        }

        @NonNull
        @Override
        public Root toObject(@NonNull Cursor c) {
            Root root = super.toObject(c);

            root.id = CursorUtils.getLong(c, TestContract.RootTable.COLUMN_ID);
            root.test = CursorUtils.getString(c, TestContract.RootTable.COLUMN_TEST);

            return root;
        }
    }
}
