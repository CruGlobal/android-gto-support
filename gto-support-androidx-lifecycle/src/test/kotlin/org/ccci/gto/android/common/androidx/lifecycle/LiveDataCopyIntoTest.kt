package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveDataCopyIntoTest : BaseLiveDataTest() {
    @Test
    fun verifyCopyIntoBehavior() {
        val source = MutableLiveData<String?>()
        val target = MutableLiveData<Any?>()

        source.copyInto(lifecycleOwner, target)
        source.value = "test"
        assertEquals("test", target.value)
        source.value = null
        assertNull(target.value)
    }
}
