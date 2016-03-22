package org.ccci.gto.android.common.db;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.db.model.Root;

import static org.ccci.gto.android.common.db.Expression.bind;
import static org.ccci.gto.android.common.db.Expression.field;

class Contract extends BaseContract {
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

        static final String SQL_CREATE_TABLE = create(TABLE_NAME, SQL_COLUMN_ID, SQL_COLUMN_TEST);
        static final String SQL_DELETE_TABLE = drop(TABLE_NAME);
    }
}
