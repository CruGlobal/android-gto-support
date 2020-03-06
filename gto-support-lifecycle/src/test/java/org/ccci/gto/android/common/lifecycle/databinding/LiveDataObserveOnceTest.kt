package org.ccci.gto.android.common.lifecycle.databinding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.lifecycle.observeOnce
import org.junit.Rule
import org.junit.Test

class LiveDataObserveOnceTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val lifecycleOwner = object : LifecycleOwner {
        val lifecycleRegistry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle = lifecycleRegistry
    }
    private val liveData = MutableLiveData<Int>()

    @Test
    fun verifyObserveOnceOnlyCalledOneTime() {
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

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
    fun verifyObserveOnceRespectsLifecycle() {
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        val observer: (Int) -> Unit = mock()
        liveData.observeOnce(lifecycleOwner, observer)

        // Lifecycle is destroyed so observer is removed before it can be called
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        liveData.value = 1
        verify(observer, never()).invoke(any())
    }
}
