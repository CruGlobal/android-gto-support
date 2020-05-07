package org.ccci.gto.android.common.db;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.db.model.Compound;
import org.ccci.gto.android.common.db.model.Root;

import static org.ccci.gto.android.common.db.Expression.bind;

public class Contract extends BaseContract {
    public static class RootTable implements Base {
        static final String TABLE_NAME = "root";
        static final Table<Root> TABLE = Table.forClass(Root.class);

        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_TEST = "test";

        static final Field FIELD_ID = TABLE.field(COLUMN_ID);

        static final String[] PROJECTION_ALL = {COLUMN_ID, COLUMN_TEST};

        static final String SQL_COLUMN_ID = COLUMN_ID + " INTEGER PRIMARY KEY";
        static final String SQL_COLUMN_TEST = COLUMN_TEST + " TEXT";

        static final Expression SQL_WHERE_PRIMARY_KEY = FIELD_ID.eq(bind());

        static final String SQL_CREATE_TABLE = create(TABLE_NAME, SQL_COLUMN_ID, SQL_COLUMN_TEST);
        static final String SQL_DELETE_TABLE = drop(TABLE_NAME);
    }

    static class CompoundTable implements Base {
        static final String TABLE_NAME = "compound";
        static final Table<Compound> TABLE = Table.forClass(Compound.class);

        static final String COLUMN_ID1 = "id1";
        static final String COLUMN_ID2 = "id2";
        static final String COLUMN_DATA1 = "data1";
        static final String COLUMN_DATA2 = "data2";

        static final Field FIELD_ID1 = TABLE.field(COLUMN_ID1);
        static final Field FIELD_ID2 = TABLE.field(COLUMN_ID2);

        static final String[] PROJECTION_ALL = {COLUMN_ID1, COLUMN_ID2, COLUMN_DATA1, COLUMN_DATA2};

        static final String SQL_COLUMN_ID1 = COLUMN_ID1 + " TEXT NOT NULL";
        static final String SQL_COLUMN_ID2 = COLUMN_ID2 + " TEXT NOT NULL";
        static final String SQL_COLUMN_DATA1 = COLUMN_DATA1 + " TEXT";
        static final String SQL_COLUMN_DATA2 = COLUMN_DATA2 + " TEXT";
        static final String SQL_PRIMARY_KEY = "UNIQUE(" + COLUMN_ID1 + "," + COLUMN_ID2 + ")";

        static final Expression SQL_WHERE_PRIMARY_KEY = FIELD_ID1.eq(bind()).and(FIELD_ID2.eq(bind()));

        static final String SQL_CREATE_TABLE =
                create(TABLE_NAME, SQL_COLUMN_ROWID, SQL_COLUMN_ID1, SQL_COLUMN_ID2, SQL_COLUMN_DATA1, SQL_COLUMN_DATA2,
                       SQL_PRIMARY_KEY);
        static final String SQL_DELETE_TABLE = drop(TABLE_NAME);
    }
}
