package org.ccci.gto.android.common.db;

import android.text.TextUtils;

import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class BaseContractTest {
    @Before
    public void setup() throws Exception {
        CommonMocks.mockTextUtils();
    }

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
