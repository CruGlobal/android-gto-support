package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MutableLiveDataTest : BaseLiveDataTest() {
    @Test
    fun testMutableLiveDataBooleanToggle() {
        val data = MutableLiveData<Boolean>()
        assertNull(data.value)
        data.toggleValue()
        assertTrue(data.value!!)
        data.toggleValue()
        assertFalse(data.value!!)
        data.toggleValue()
        assertTrue(data.value!!)
    }
}
