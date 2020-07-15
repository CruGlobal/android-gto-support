package org.ccci.gto.android.common.eventbus.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventBusLifecycleRegisterTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val lifecycleOwner = object : LifecycleOwner {
        val lifecycleRegistry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle = lifecycleRegistry
    }
    private lateinit var eventBus: EventBus
    private val subscriber = Any()

    @Before
    fun setup() {
        eventBus = mock()
    }

    @Test
    fun verifyRegisterBeforeStart() {
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        eventBus.register(lifecycleOwner, subscriber)
        verify(eventBus, never()).register(any())
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        verify(eventBus).register(subscriber)
        verify(eventBus, never()).unregister(any())
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(eventBus).unregister(any())
    }

    @Test
    fun verifyRegisterAfterStart() {
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        eventBus.register(lifecycleOwner, subscriber)
        verify(eventBus).register(subscriber)
        verify(eventBus, never()).unregister(any())
        lifecycleOwner.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(eventBus).unregister(any())
    }
}
