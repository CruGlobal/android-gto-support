package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.clear
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class LiveDataViewModelObserverTest : BaseLiveDataTest() {
    private val liveData = MutableLiveData<Int>()
    private lateinit var viewModel: ViewModel

    @Before
    fun setup() {
        viewModel = object : ViewModel() {}
    }

    @After
    fun cleanup() {
        viewModel.clear()
    }

    @Test
    fun verifyLiveDataViewModelObserver() {
        liveData.observe(viewModel, observer)
        liveData.value = 1
        verify(observer).onChanged(any())

        viewModel.clear()
        liveData.value = 2
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun verifyLiveDataViewModelObserverCanBeRemoved() {
        val resp = liveData.observe(viewModel, observer)
        liveData.value = 1
        verify(observer).onChanged(any())

        liveData.removeObserver(resp)
        liveData.value = 2
        verifyNoMoreInteractions(observer)
    }

    // region Multi-observe
    private val data2 = MutableLiveData<String>()
    private val data3 = MutableLiveData<Long>()

    @Test
    fun `observe() - 2 LiveDatas`() {
        viewModel.observe(liveData, data2) { t1, t2 -> observer.onChanged("$t1 $t2") }

        liveData.value = 1
        data2.value = "a"
        liveData.value = 2
        data2.value = "b"
        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("1 a", "2 a", "2 b"), allValues)
        }
    }

    @Test
    fun `observe() - 3 LiveDatas`() {
        viewModel.observe(liveData, data2, data3) { d1, d2, d3 -> observer.onChanged("$d1 $d2 $d3") }
        liveData.value = 1
        data2.value = "a"
        data3.value = 100
        liveData.value = 2
        data2.value = "b"
        data3.value = 200
        argumentCaptor<String> {
            verify(observer, times(4)).onChanged(capture())
            verifyNoMoreInteractions(observer)
            assertEquals(listOf("1 a 100", "2 a 100", "2 b 100", "2 b 200"), allValues)
        }
    }
    // endregion Multi-observe
}
