package org.ccci.gto.android.common.db;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.util.Pair;

import org.ccci.gto.android.common.db.Contract.RootTable;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class QueryTest extends InstrumentationTestCase {
    private TestDao getDao() {
        return TestDao.getInstance(getInstrumentation().getContext());
    }

    @Test
    public void testHavingSql() {
        Expression having = RootTable.FIELD_TEST.count().eq(1);
        Query query = Query.select(RootTable.class).groupBy(RootTable.FIELD_TEST).having(having);
        Pair<String, String[]> sqlPair = query.buildSqlHaving(getDao());
        assertThat(sqlPair.first, is("(COUNT (root.test) == 1)"));
    }
}
