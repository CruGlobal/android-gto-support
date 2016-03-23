package org.ccci.gto.android.common.db;

import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.ccci.gto.android.common.db.Contract.RootTable;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Pair.class, TextUtils.class})
public class ExpressionTest {
    private TestDao getDao() {
        return TestDao.mock();
    }

    @Before
    public void setup() throws Exception {
        CommonMocks.mockPair();
        CommonMocks.mockTextUtils();
    }

    @Test
    public void testEqualsSql() {
        final TestDao dao = getDao();

        Expression equalsExpression = RootTable.FIELD_TEST.eq("1");
        assertThat(equalsExpression.buildSql(dao).first, is("(root.test == ?)"));
    }

    @Test
    public void testNotEqualsSql() {
        final TestDao dao = getDao();

        Expression notEqualsExpression = RootTable.FIELD_TEST.ne("1");
        assertThat(notEqualsExpression.buildSql(dao).first, is("(root.test != ?)"));
    }

    @Test
    public void testCount() {
        final TestDao dao = getDao();

        Expression countExpression = RootTable.FIELD_TEST.count();
        assertThat(countExpression.buildSql(dao).first, is("COUNT (root.test)"));
    }

    @Test
    public void testCountInHaving() {
        final TestDao dao = getDao();

        Expression countExpression = RootTable.FIELD_TEST.count().eq(1);
        assertThat(countExpression.buildSql(dao).first, is("(COUNT (root.test) == 1)"));
    }
}
