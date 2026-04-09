package org.ccci.gto.android.common.kotlin.coroutines.flow

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowValueTest {
    @Test
    fun verifyEquals() {
        assertEquals(StateFlowValue("key1"), StateFlowValue("key1"))
        assertNotEquals(StateFlowValue("key1"), StateFlowValue.Initial("key1"))
    }

    @Test
    fun verifyIsInitial() {
        assertTrue(StateFlowValue.Initial("").isInitial)
        assertFalse(StateFlowValue("").isInitial)
    }
}
