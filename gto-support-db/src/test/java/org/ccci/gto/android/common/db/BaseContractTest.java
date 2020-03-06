package org.ccci.gto.android.common.db;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseContractTest {
    @Test(expected = IllegalArgumentException.class)
    public void verifyUniqueIndexNoFields() throws Exception {
        BaseContract.uniqueIndex();
    }

    @Test
    public void verifyUniqueIndex() throws Exception {
        assertThat(BaseContract.uniqueIndex("field1").trim(), is("UNIQUE(field1)"));
        assertThat(BaseContract.uniqueIndex("field1", "field2").trim(), is("UNIQUE(field1,field2)"));
    }
}
