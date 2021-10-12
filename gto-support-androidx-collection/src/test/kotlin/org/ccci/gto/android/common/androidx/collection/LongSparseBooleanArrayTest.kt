package org.ccci.gto.android.common.androidx.collection

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LongSparseBooleanArrayTest {
    @Test
    fun verifyGetValueIfMissingBehavior() {
        val obj = LongSparseBooleanArray()

        // assertThat(obj.indexOfKey(1L), lessThanOrEqualTo(-1))
        assertFalse(obj.get(1L))
        assertFalse(obj.get(1L, false))
        assertTrue(obj.get(1L, true))
    }
}
