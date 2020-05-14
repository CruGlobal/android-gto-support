package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TransformationsCombineWithTest : BaseLiveDataTest() {
    private val str1 = MutableLiveData<String>()
    private val str2 = MutableLiveData<String?>()
    private val str3 = MutableLiveData<String?>()
    private val str4 = MutableLiveData<String?>(null)

    @Test
    fun verifySwitchCombineWith() {
        val combined = str1.switchCombineWith(str2) { a, b -> MutableLiveData(listOfNotNull(a, b).joinToString()) }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = "c"
        verify(observer).onChanged(any())
        assertEquals("b, c", combined.value)
        argumentCaptor<String> {
            verify(observer).onChanged(capture())
            assertThat(allValues, contains("b, c"))
        }
    }

    @Test
    fun verifySwitchCombineWithObserverCalledOnceOnInitialization() {
        str1.value = "a"
        str2.value = "b"
        str3.value = "c"
        val combined =
            str1.switchCombineWith(str2, str3) { a, b, c -> MutableLiveData(listOfNotNull(a, b, c).joinToString()) }
        combined.observeForever(observer)

        argumentCaptor<String> {
            verify(observer).onChanged(capture())
            assertThat(allValues, contains("a, b, c"))
        }
    }
    @Test
    fun verifyCombineWith2() {
        val combined = str1.combineWith(str2) { a, b -> listOfNotNull(a, b).joinToString() }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer).onChanged(any())
        assertEquals("b", combined.value)
        str2.value = "c"
        verify(observer, times(2)).onChanged(any())
        assertEquals("b, c", combined.value)
        argumentCaptor<String> {
            verify(observer, times(2)).onChanged(capture())
            assertThat(allValues, contains("b", "b, c"))
        }
    }

    @Test
    fun verifyCombineWith3() {
        val combined = str1.combineWith(str2, str3) { a, b, c -> listOfNotNull(a, b, c).joinToString() }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str3.value = null
        verify(observer).onChanged(any())
        assertEquals("b", combined.value)
        str3.value = "d"
        verify(observer, times(2)).onChanged(any())
        assertEquals("b, d", combined.value)
        str2.value = "c"
        verify(observer, times(3)).onChanged(any())
        assertEquals("b, c, d", combined.value)
        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            assertThat(allValues, contains("b", "b, d", "b, c, d"))
        }
    }

    @Test
    fun verifyCombineWith4() {
        val combined = str1.combineWith(str2, str3, str4) { a, b, c, d -> listOfNotNull(a, b, c, d).joinToString() }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str3.value = null
        verify(observer).onChanged(any())
        assertEquals("b", combined.value)
        str4.value = "e"
        verify(observer, times(2)).onChanged(any())
        assertEquals("b, e", combined.value)
        str3.value = "d"
        verify(observer, times(3)).onChanged(any())
        assertEquals("b, d, e", combined.value)
        str2.value = "c"
        verify(observer, times(4)).onChanged(any())
        assertEquals("b, c, d, e", combined.value)
        argumentCaptor<String> {
            verify(observer, times(4)).onChanged(capture())
            assertThat(allValues, contains("b", "b, e", "b, d, e", "b, c, d, e"))
        }
    }

    @Test
    fun verifyCombineWithObserverCalledOnceOnInitialization() {
        str1.value = "a"
        str2.value = "b"
        str3.value = "c"
        val combined = str1.combineWith(str2, str3) { a, b, c -> listOfNotNull(a, b, c).joinToString() }
        combined.observeForever(observer)

        argumentCaptor<String> {
            verify(observer).onChanged(capture())
            assertThat(allValues, contains("a, b, c"))
        }
    }
}
