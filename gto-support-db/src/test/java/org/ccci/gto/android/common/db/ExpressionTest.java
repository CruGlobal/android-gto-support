package org.ccci.gto.android.common.db;

import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.ccci.gto.android.common.db.Contract.RootTable;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Pair.class, TextUtils.class})
public class ExpressionTest {
    private AbstractDao mDao;

    @Before
    public void setup() throws Exception {
        CommonMocks.mockPair();
        CommonMocks.mockTextUtils();

        mDao = mock(AbstractDao.class);
        when(mDao.getTable(eq(Root.class))).thenReturn(RootTable.TABLE_NAME);
    }

    @Test
    public void testEqualsSql() {
        Expression equalsExpression = RootTable.FIELD_TEST.eq("1");
        assertThat(equalsExpression.buildSql(mDao).first, is("(root.test == ?)"));
    }

    @Test
    public void testNotEqualsSql() {
        Expression notEqualsExpression = RootTable.FIELD_TEST.ne("1");
        assertThat(notEqualsExpression.buildSql(mDao).first, is("(root.test != ?)"));
    }

    @Test
    public void testCount() {
        Expression countExpression = RootTable.FIELD_TEST.count();
        assertThat(countExpression.buildSql(mDao).first, is("COUNT (root.test)"));
    }

    @Test
    public void testCountInHaving() {
        Expression countExpression = RootTable.FIELD_TEST.count().eq(1);
        assertThat(countExpression.buildSql(mDao).first, is("(COUNT (root.test) == 1)"));
    }
}
