package org.ccci.gto.android.common.db;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.ccci.gto.android.common.db.TestDao.TestContract.RootTable;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExpressionTest extends InstrumentationTestCase {

    private TestDao getDao() {
        return new TestDao(getInstrumentation().getContext());
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

        Expression notEqualsExpression = RootTable.FIELD_TEST.not().eq("1");
        assertThat(notEqualsExpression.buildSql(dao).first, is("(NOT (root.test) == ?)"));
    }
}
