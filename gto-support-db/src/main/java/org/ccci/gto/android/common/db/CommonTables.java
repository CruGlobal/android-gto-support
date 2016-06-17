package org.ccci.gto.android.common.db;

import org.ccci.gto.android.common.db.Expression.Field;

import static org.ccci.gto.android.common.db.Expression.bind;

public final class CommonTables extends BaseContract {
    public static final class LastSyncTable implements Base {
        static final String TABLE_NAME = "syncData";
        private static final Table<LastSyncTable> TABLE = Table.forClass(LastSyncTable.class);

        static final String COLUMN_KEY = "key";
        static final String COLUMN_LAST_SYNCED = "lastSynced";

        private static final Field FIELD_KEY = TABLE.field(COLUMN_KEY);

        private static final String SQL_COLUMN_KEY = COLUMN_KEY + " TEXT NOT NULL";
        private static final String SQL_COLUMN_LAST_SYNCED = COLUMN_LAST_SYNCED + " INTEGER";
        private static final String SQL_PRIMARY_KEY = uniqueIndex(COLUMN_KEY);

        static final Expression SQL_WHERE_PRIMARY_KEY = FIELD_KEY.eq(bind());

        public static final String SQL_CREATE_TABLE =
                create(TABLE_NAME, SQL_COLUMN_ROWID, SQL_COLUMN_KEY, SQL_COLUMN_LAST_SYNCED, SQL_PRIMARY_KEY);
        public static final String SQL_DELETE_TABLE = drop(TABLE_NAME);
    }
}
