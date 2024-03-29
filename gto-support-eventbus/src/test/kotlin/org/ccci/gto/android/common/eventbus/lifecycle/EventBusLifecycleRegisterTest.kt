package org.ccci.gto.android.common.eventbus.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class EventBusLifecycleRegisterTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val lifecycleOwner = TestLifecycleOwner(coroutineDispatcher = UnconfinedTestDispatcher())
    private lateinit var eventBus: EventBus
    private val subscriber = Any()

    @Before
    fun setup() {
        eventBus = mock()
    }

    @Test
    fun verifyRegisterBeforeStart() {
        lifecycleOwner.currentState = Lifecycle.State.CREATED

        eventBus.register(lifecycleOwner, subscriber)
        verify(eventBus, never()).register(any())
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        verify(eventBus).register(subscriber)
        verify(eventBus, never()).unregister(any())
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(eventBus).unregister(any())
    }

    @Test
    fun verifyRegisterAfterStart() {
        lifecycleOwner.currentState = Lifecycle.State.RESUMED

        eventBus.register(lifecycleOwner, subscriber)
        verify(eventBus).register(subscriber)
        verify(eventBus, never()).unregister(any())
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        verify(eventBus).unregister(any())
    }
}
