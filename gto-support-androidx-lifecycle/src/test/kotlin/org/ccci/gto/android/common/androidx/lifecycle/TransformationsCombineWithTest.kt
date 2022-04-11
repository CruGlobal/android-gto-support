package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TransformationsCombineWithTest : BaseLiveDataTest() {
    private val str1 = MutableLiveData<String>()
    private val str2 = MutableLiveData<String?>()
    private val str3 = MutableLiveData<String?>()
    private val str4 = MutableLiveData<String?>(null)
    private val str5 = MutableLiveData<String?>()
    private val str6 = MutableLiveData<String?>()

    // region switchCombine() & switchCombineWith()
    @Test
    fun `switchCombine() - 2 LiveDatas`() {
        val combined = switchCombine(str1, str2) { a, b -> MutableLiveData(listOfNotNull(a, b).joinToString()) }
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
    fun `switchCombineWith() - 2 LiveDatas`() {
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
    // endregion switchCombine() & switchCombineWith()

    // region combine() & combineWith()
    @Test
    fun `combine() - 2 LiveDatas`() {
        val combined = combine(str1, str2) { a, b -> listOfNotNull(a, b).joinToString() }
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
    fun `combineWith() - 2 LiveDatas`() {
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
    fun `combine() - 3 LiveDatas`() {
        val combined = combine(str1, str2, str3) { a, b, c -> listOfNotNull(a, b, c).joinToString() }
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
    fun `combineWith() - 3 LiveDatas`() {
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
    fun `combine() - 4 LiveDatas`() {
        val combined = combine(str1, str2, str3, str4) { a, b, c, d -> listOfNotNull(a, b, c, d).joinToString() }
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
    fun `combineWith() - 4 LiveDatas`() {
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
    fun `combine() - 5 LiveDatas`() {
        val combined =
            combine(str1, str2, str3, str4, str5) { a, b, c, d, e -> listOfNotNull(a, b, c, d, e).joinToString() }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str5.value = null
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
        str5.value = "f"
        verify(observer, times(4)).onChanged(any())
        assertEquals("b, d, e, f", combined.value)
        str2.value = "c"
        verify(observer, times(5)).onChanged(any())
        assertEquals("b, c, d, e, f", combined.value)
        argumentCaptor<String> {
            verify(observer, times(5)).onChanged(capture())
            assertThat(allValues, contains("b", "b, e", "b, d, e", "b, d, e, f", "b, c, d, e, f"))
        }
    }

    @Test
    fun `combineWith() - 5 LiveDatas`() {
        val combined =
            str1.combineWith(str2, str3, str4, str5) { a, b, c, d, e -> listOfNotNull(a, b, c, d, e).joinToString() }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str5.value = null
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
        str5.value = "f"
        verify(observer, times(4)).onChanged(any())
        assertEquals("b, d, e, f", combined.value)
        str2.value = "c"
        verify(observer, times(5)).onChanged(any())
        assertEquals("b, c, d, e, f", combined.value)
        argumentCaptor<String> {
            verify(observer, times(5)).onChanged(capture())
            assertThat(allValues, contains("b", "b, e", "b, d, e", "b, d, e, f", "b, c, d, e, f"))
        }
    }

    @Test
    fun `combineWith() - 6 LiveDatas`() {
        val combined = str1.combineWith(str2, str3, str4, str5, str6) { a, b, c, d, e, f ->
            listOfNotNull(a, b, c, d, e, f).joinToString()
        }
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str5.value = null
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str6.value = null
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
        str5.value = "f"
        verify(observer, times(4)).onChanged(any())
        assertEquals("b, d, e, f", combined.value)
        str2.value = "c"
        verify(observer, times(5)).onChanged(any())
        assertEquals("b, c, d, e, f", combined.value)
        str6.value = "g"
        verify(observer, times(6)).onChanged(any())
        assertEquals("b, c, d, e, f, g", combined.value)
        argumentCaptor<String> {
            verify(observer, times(6)).onChanged(capture())
            assertThat(allValues, contains("b", "b, e", "b, d, e", "b, d, e, f", "b, c, d, e, f", "b, c, d, e, f, g"))
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
    // endregion combine() & combineWith()
}
