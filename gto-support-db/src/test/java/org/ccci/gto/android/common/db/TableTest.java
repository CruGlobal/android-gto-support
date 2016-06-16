package org.ccci.gto.android.common.db;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class TableTest {
    @NonNull
    private TestDao getDao() {
        return new TestDao();
    }

    @Before
    public void setup() throws Exception {
        CommonMocks.mockTextUtils();
    }

    @Test
    public void testBuildSqlName() throws Exception {
        final Table<Obj1> t1 = Table.forClass(Obj1.class);
        final Table<Obj2> t2 = Table.forClass(Obj2.class);

        assertEquals(Obj1.TABLE_NAME, t1.sqlTable(getDao()));
        assertEquals(Obj1.TABLE_NAME + " AS a", t1.as("a").sqlTable(getDao()));
        assertEquals(Obj1.TABLE_NAME + " AS abcde", t1.as("abcde").sqlTable(getDao()));
        assertNotEquals(Obj2.TABLE_NAME, t1.sqlTable(getDao()));

        assertEquals(Obj2.TABLE_NAME, t2.sqlTable(getDao()));
        assertEquals(Obj2.TABLE_NAME + " AS b", t2.as("b").sqlTable(getDao()));
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
