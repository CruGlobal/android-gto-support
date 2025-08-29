package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TransformationsBooleanOperatorsTest : BaseLiveDataTest() {
    private val bool1 = MutableLiveData(false)
    private val bool2 = MutableLiveData(false)

    @Test
    fun verifyLiveDataAnd() {
        val liveData = bool1 and bool2

        liveData.observeForever(observer)
        assertFalse(liveData.value!!)
        bool1.value = true
        assertFalse(liveData.value!!)
        bool2.value = true
        assertTrue(liveData.value!!)
        bool1.value = false
        assertFalse(liveData.value!!)
        argumentCaptor<Boolean> {
            verify(observer, times(4)).onChanged(capture())
            assertThat(allValues, contains(false, false, true, false))
        }
    }

    @Test
    fun verifyLiveDataOr() {
        val liveData = bool1 or bool2

        liveData.observeForever(observer)
        assertFalse(liveData.value!!)
        bool1.value = true
        assertTrue(liveData.value!!)
        bool2.value = true
        assertTrue(liveData.value!!)
        bool1.value = false
        assertTrue(liveData.value!!)
        bool2.value = false
        assertFalse(liveData.value!!)
        argumentCaptor<Boolean> {
            verify(observer, times(5)).onChanged(capture())
            assertThat(allValues, contains(false, true, true, true, false))
        }
    }
}
