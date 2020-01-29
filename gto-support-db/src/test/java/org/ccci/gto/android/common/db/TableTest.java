package org.ccci.gto.android.common.db;

import android.text.TextUtils;

import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class TableTest {
    private AbstractDao mDao;

    @Before
    public void setup() throws Exception {
        CommonMocks.mockTextUtils();

        mDao = mock(AbstractDao.class);
        when(mDao.getTable(eq(Obj1.class))).thenReturn(Obj1.TABLE_NAME);
        when(mDao.getTable(eq(Obj2.class))).thenReturn(Obj2.TABLE_NAME);
    }

    @Test
    public void testBuildSqlName() throws Exception {
        final Table<Obj1> t1 = Table.forClass(Obj1.class);
        final Table<Obj2> t2 = Table.forClass(Obj2.class);

        assertEquals(Obj1.TABLE_NAME, t1.sqlTable(mDao));
        assertEquals(Obj1.TABLE_NAME + " AS a", t1.as("a").sqlTable(mDao));
        assertEquals(Obj1.TABLE_NAME + " AS abcde", t1.as("abcde").sqlTable(mDao));
        assertNotEquals(Obj2.TABLE_NAME, t1.sqlTable(mDao));

        assertEquals(Obj2.TABLE_NAME, t2.sqlTable(mDao));
        assertEquals(Obj2.TABLE_NAME + " AS b", t2.as("b").sqlTable(mDao));
    }

    static class Obj1 {
        static final String TABLE_NAME = "Table1";
    }

    static class Obj2 {
        static final String TABLE_NAME = "Table2";
    }
}
