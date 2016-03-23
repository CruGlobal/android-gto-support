package org.ccci.gto.android.common.db;

import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Pair.class, TextUtils.class})
public class QueryTest {
    private TestDao getDao() {
        return TestDao.mock();
    }

    @Before
    public void setup() throws Exception {
        CommonMocks.mockPair();
        CommonMocks.mockTextUtils();
    }

    @Test
    public void testHavingSql() {
        Expression having = RootTable.FIELD_TEST.count().eq(1);
        Query query = Query.select(RootTable.class).groupBy(RootTable.FIELD_TEST).having(having);
        Pair<String, String[]> sqlPair = query.buildSqlHaving(getDao());
        assertThat(sqlPair.first, is("(COUNT (root.test) == 1)"));
    }
}
