package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SavedStateHandleDelegatesTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val savedState = SavedStateHandle()
    private val delegates = object {
        var propStr: String? by savedState.delegate()
        var propStrNotNull: String by savedState.delegate("propStr", ifNull = "default")
        val propStrLiveData: MutableLiveData<String> by savedState.livedata("propStr")
    }

    @Test
    fun testPropertyDelegate() {
        delegates.propStr = "test"
        assertEquals("test", savedState["propStr"])
        assertEquals("test", delegates.propStr)
        assertEquals("test", delegates.propStrNotNull)
        assertEquals("test", delegates.propStrLiveData.value)
    }

    @Test
    fun testPropertyDelegateWhenNull() {
        delegates.propStr = null
        assertNull(delegates.propStr)
        assertEquals("default", delegates.propStrNotNull)
    }

    @Test
    fun testLiveDataDelegate() {
        delegates.propStrLiveData.value = "livedata"
        assertEquals("livedata", savedState["propStr"])
        assertEquals("livedata", delegates.propStr)
        assertEquals("livedata", delegates.propStrLiveData.value)
    }
}
