package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.clear
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LiveDataViewModelObserver {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val liveData = MutableLiveData<Int>()
    private lateinit var viewModel: ViewModel
    private lateinit var onChanged: (Int) -> Unit

    @Before
    fun setup() {
        viewModel = object : ViewModel() {}
        onChanged = mock()
    }

    @After
    fun cleanup() {
        viewModel.clear()
    }

    @Test
    fun verifyLiveDataViewModelObserver() {
        liveData.observe(viewModel, onChanged)
        liveData.value = 1
        verify(onChanged).invoke(any())

        reset(onChanged)
        viewModel.clear()
        liveData.value = 2
        verify(onChanged, never()).invoke(any())
    }

    @Test
    fun verifyLiveDataViewModelObserverCanBeRemoved() {
        val observer = liveData.observe(viewModel, onChanged)
        liveData.value = 1
        verify(onChanged).invoke(any())

        reset(onChanged)
        liveData.removeObserver(observer)
        liveData.value = 2
        verify(onChanged, never()).invoke(any())
    }
}
