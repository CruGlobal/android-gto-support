package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TransformationsNotNullTest : BaseLiveDataTest() {
    @Test
    fun verifyLiveDataNotNull() {
        val source = MutableLiveData<String?>(null)
        val result = source.notNull()
        result.observeForever(observer)

        source.value = null
        verify(observer, never()).onChanged(any())
        source.value = "a"
        assertEquals("a", result.value)
        source.value = null
        assertEquals("a", result.value)
        source.value = "b"
        source.value = "b"
        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            assertThat(allValues, contains("a", "b", "b"))
        }
    }
}
