package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

class LiveDataObserveOnceTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.STARTED, UnconfinedTestDispatcher())
    private val liveData = MutableLiveData<Int>()

    @Test
    fun verifyObserveOnceOnlyCalledOneTime() {
        // observe before LiveData has data
        val observer: (Int) -> Unit = mock()
        liveData.observeOnce(lifecycleOwner, observer)
        verify(observer, never()).invoke(any())
        liveData.value = 1
        verify(observer).invoke(eq(1))
        reset(observer)
        liveData.value = 2
        verify(observer, never()).invoke(any())

        // observe after LiveData has data
        val observer2: (Int) -> Unit = mock()
        liveData.observeOnce(lifecycleOwner, observer2)
        verify(observer2).invoke(eq(2))
        reset(observer2)
        liveData.value = 3
        verify(observer2, never()).invoke(any())
    }

    @Test
    fun verifyObserveOnceWithoutLifecycle() {
        // observe before LiveData has data
        val observer: (Int) -> Unit = mock()
        liveData.observeOnce(observer)
        verify(observer, never()).invoke(any())
        liveData.value = 1
        verify(observer).invoke(eq(1))
        reset(observer)
        liveData.value = 2
        verify(observer, never()).invoke(any())

        // observe after LiveData has data
        val observer2: (Int) -> Unit = mock()
        liveData.observeOnce(observer2)
        verify(observer2).invoke(eq(2))
        reset(observer2)
        liveData.value = 3
        verify(observer2, never()).invoke(any())
    }

    @Test
    fun verifyObserveOnceRespectsLifecycle() {
        val observer: (Int) -> Unit = mock()
        liveData.observeOnce(lifecycleOwner, observer)

        // Lifecycle is destroyed so observer is removed before it can be called
        lifecycleOwner.currentState = Lifecycle.State.DESTROYED
        liveData.value = 1
        verify(observer, never()).invoke(any())
    }
}
