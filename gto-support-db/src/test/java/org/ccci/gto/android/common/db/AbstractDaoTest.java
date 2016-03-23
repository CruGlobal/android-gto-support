package org.ccci.gto.android.common.db;

import android.text.TextUtils;
import android.util.Pair;

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
public class AbstractDaoTest {
    private TestDao getDao() {
        return TestDao.mock();
    }

    @Before
    public void setup() throws Exception {
        CommonMocks.mockPair();
        CommonMocks.mockTextUtils();
    }

    @Test
    public void testAddPrefixToSingleField() {
        final TestDao dao = getDao();
        String prefix = Contract.RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(Contract.RootTable.COLUMN_ID, prefix);

        assertThat(editedClause, is("root._id"));
    }

    @Test
    public void testAddPrefixToMultipleFields() {
        final TestDao dao = getDao();
        String prefix = Contract.RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(Contract.RootTable.COLUMN_ID + "," + Contract.RootTable.COLUMN_TEST,
                                                    prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsSomePrefixed() {
        final TestDao dao = getDao();
        String prefix = Contract.RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(
                prefix + Contract.RootTable.COLUMN_ID + "," + Contract.RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsAllPrefixed() {
        final TestDao dao = getDao();
        String prefix = Contract.RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(
                prefix + Contract.RootTable.COLUMN_ID + "," + prefix + Contract.RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }
}
