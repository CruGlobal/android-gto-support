package org.ccci.gto.android.common.db

import org.ccci.gto.android.common.db.BaseContract.Companion.uniqueIndex
import org.junit.Assert.assertEquals
import org.junit.Test

class BaseContractTest {
    @Test(expected = IllegalArgumentException::class)
    fun verifyUniqueIndexNoFieldsThrowsException() {
        uniqueIndex()
    }

    @Test
    fun verifyUniqueIndex() {
        assertEquals("UNIQUE(field1)", uniqueIndex("field1"))
        assertEquals("UNIQUE(field1,field2)", uniqueIndex("field1", "field2"))
    }
}
