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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Pair.class, TextUtils.class})
public class AbstractDaoTest {
    private AbstractDao mDao;

    @Before
    public void setup() throws Exception {
        CommonMocks.mockPair();
        CommonMocks.mockTextUtils();

        mDao = mock(AbstractDao.class);
        when(mDao.addPrefixToFields(any(), any())).thenCallRealMethod();
    }

    @Test
    public void testAddPrefixToSingleField() {
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = mDao.addPrefixToFields(RootTable.COLUMN_ID, prefix);

        assertThat(editedClause, is("root._id"));
    }

    @Test
    public void testAddPrefixToMultipleFields() {
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = mDao.addPrefixToFields(RootTable.COLUMN_ID + "," + RootTable.COLUMN_TEST,
                                                     prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsSomePrefixed() {
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = mDao.addPrefixToFields(
                prefix + RootTable.COLUMN_ID + "," + RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsAllPrefixed() {
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = mDao.addPrefixToFields(
                prefix + RootTable.COLUMN_ID + "," + prefix + RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }
}
