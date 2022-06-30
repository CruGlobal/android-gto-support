package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@OptIn(ExperimentalCoroutinesApi::class)
class LiveDataMultiObserveTest : BaseLiveDataTest() {
    private val data1 = MutableLiveData<String>()
    private val data2 = MutableLiveData<Int>()
    private val data3 = MutableLiveData<Long>()

    @Test
    fun `observe() - 2 LiveDatas`() {
        lifecycleOwner.observe(data1, data2) { d1, d2 -> observer.onChanged("$d1 $d2") }
        data1.value = "a"
        data2.value = 1
        data1.value = "b"
        data2.value = 2
        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("a 1", "b 1", "b 2"), allValues)
        }
    }

    @Test
    fun `observe() - 3 LiveDatas`() {
        lifecycleOwner.observe(data1, data2, data3) { d1, d2, d3 -> observer.onChanged("$d1 $d2 $d3") }
        data1.value = "a"
        data2.value = 1
        data3.value = 100
        data1.value = "b"
        data2.value = 2
        data3.value = 200
        argumentCaptor<String> {
            verify(observer, times(4)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("a 1 100", "b 1 100", "b 2 100", "b 2 200"), allValues)
        }
    }

    @Test
    fun `observeForever() - 2 LiveDatas`() {
        observeForever(data1, data2) { d1, d2 -> observer.onChanged("$d1 $d2") }
        data1.value = "a"
        data2.value = 1
        data1.value = "b"
        data2.value = 2
        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("a 1", "b 1", "b 2"), allValues)
        }
    }

    @Test
    fun `observeForever() - 3 LiveDatas`() {
        observeForever(data1, data2, data3) { d1, d2, d3 -> observer.onChanged("$d1 $d2 $d3") }
        data1.value = "a"
        data2.value = 1
        data3.value = 100
        data1.value = "b"
        data2.value = 2
        data3.value = 200
        argumentCaptor<String> {
            verify(observer, times(4)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("a 1 100", "b 1 100", "b 2 100", "b 2 200"), allValues)
        }
    }
}
