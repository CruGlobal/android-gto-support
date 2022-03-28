package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.clear
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
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
}
