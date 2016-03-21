package org.ccci.gto.android.common.db;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.ccci.gto.android.common.db.TestDao.TestContract.RootTable;
import org.ccci.gto.android.common.db.model.Root;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AbstractDaoTest extends InstrumentationTestCase {
    private TestDao getDao() {
        return new TestDao(getInstrumentation().getContext());
    }

    @Test
    public void testGetTable() throws Exception {
        final TestDao dao = getDao();
        assertThat(dao.getTable(Root.class), is(RootTable.TABLE_NAME));
    }
}
