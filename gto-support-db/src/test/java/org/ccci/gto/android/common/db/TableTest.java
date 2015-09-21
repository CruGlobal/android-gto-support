package org.ccci.gto.android.common.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.support.annotation.NonNull;

import org.junit.Test;

public class TableTest {
    private final TestDao dao = new TestDao();

    @Test
    public void testBuildSqlName() throws Exception {
        final Table<Obj1> t1 = Table.forClass(Obj1.class);
        final Table<Obj2> t2 = Table.forClass(Obj2.class);

        assertEquals(Obj1.TABLE_NAME, t1.buildSqlName(dao));
        assertEquals(Obj1.TABLE_NAME + " AS a", t1.as("a").buildSqlName(dao));
        assertEquals(Obj1.TABLE_NAME + " AS abcde", t1.as("abcde").buildSqlName(dao));
        assertNotEquals(Obj2.TABLE_NAME, t1.buildSqlName(dao));

        assertEquals(Obj2.TABLE_NAME, t2.buildSqlName(dao));
        assertEquals(Obj2.TABLE_NAME + " AS b", t2.as("b").buildSqlName(dao));
    }

    static class Obj1 {
        final static String TABLE_NAME = "Table1";
    }

    static class Obj2 {
        final static String TABLE_NAME = "Table2";
    }

    private static class TestDao extends AbstractDao {
        @SuppressWarnings("ConstantConditions")
        protected TestDao() {
            super(null);
        }

        @NonNull
        @Override
        protected String getTable(@NonNull final Class<?> clazz) {
            if (Obj1.class.equals(clazz)) {
                return Obj1.TABLE_NAME;
            } else if (Obj2.class.equals(clazz)) {
                return Obj2.TABLE_NAME;
            } else {
                return super.getTable(clazz);
            }
        }
    }
}
