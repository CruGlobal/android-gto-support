package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class LambdaLifecycleObserverTest {
    private lateinit var lambda: (LifecycleOwner) -> Unit

    private val lifecycleOwner = object : LifecycleOwner {
        val lifecycleRegistry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle = lifecycleRegistry
    }

    @Before
    fun setup() {
        lambda = mock()
    }

    @Test
    fun testOnDestroy() {
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.lifecycle.onDestroy(lambda)

        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(lambda, never()).invoke(any())
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        verify(lambda).invoke(eq(lifecycleOwner))
    }
}
