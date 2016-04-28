package org.ccci.gto.android.common.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TableTest {
    private final TestDao dao = new TestDao();

    @Test
    public void testBuildSqlName() throws Exception {
        final Table<Obj1> t1 = Table.forClass(Obj1.class);
        final Table<Obj2> t2 = Table.forClass(Obj2.class);

        assertEquals(Obj1.TABLE_NAME, t1.sqlTable(dao));
        assertEquals(Obj1.TABLE_NAME + " AS a", t1.as("a").sqlTable(dao));
        assertEquals(Obj1.TABLE_NAME + " AS abcde", t1.as("abcde").sqlTable(dao));
        assertNotEquals(Obj2.TABLE_NAME, t1.sqlTable(dao));

        assertEquals(Obj2.TABLE_NAME, t2.sqlTable(dao));
        assertEquals(Obj2.TABLE_NAME + " AS b", t2.as("b").sqlTable(dao));
    }

    static class Obj1 {
        static final String TABLE_NAME = "Table1";
    }

    static class Obj2 {
        static final String TABLE_NAME = "Table2";
    }

    private static class TestDao extends AbstractDao {
        @SuppressWarnings("ConstantConditions")
        protected TestDao() {
            super(null);
            registerType(Obj1.class, Obj1.TABLE_NAME, null, null, null);
            registerType(Obj2.class, Obj2.TABLE_NAME, null, null, null);
        }
    }
}
