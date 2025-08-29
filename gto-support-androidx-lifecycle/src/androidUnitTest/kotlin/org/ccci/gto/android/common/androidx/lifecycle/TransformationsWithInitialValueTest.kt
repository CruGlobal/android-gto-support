package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TransformationsWithInitialValueTest : BaseLiveDataTest() {
    @Test
    fun verifyWithInitialValue() {
        val source = MutableLiveData<String>()

        val liveData = source.withInitialValue("a")
        liveData.observeForever(observer)
        verify(observer).onChanged(any())
        assertEquals("a", liveData.value)
        source.value = "a"
        assertEquals("a", liveData.value)
        source.value = "b"
        assertEquals("b", liveData.value)

        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            assertThat(allValues, contains("a", "a", "b"))
        }
    }

    @Test
    fun verifyWithInitialValueWhenSourceIsAlreadyInitialized() {
        val source = MutableLiveData<String>("b")

        val liveData = source.withInitialValue("a")
        liveData.observeForever(observer)
        verify(observer).onChanged(any())
        assertEquals("b", liveData.value)
        source.value = "c"
        assertEquals("c", liveData.value)

        argumentCaptor<String> {
            verify(observer, times(2)).onChanged(capture())
            assertThat(allValues, contains("b", "c"))
        }
    }
}
