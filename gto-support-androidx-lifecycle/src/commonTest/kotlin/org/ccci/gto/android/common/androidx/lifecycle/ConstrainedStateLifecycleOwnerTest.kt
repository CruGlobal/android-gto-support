package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class ConstrainedStateLifecycleOwnerTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val parentLifecycleOwner = TestLifecycleOwner(Lifecycle.State.CREATED, UnconfinedTestDispatcher())
    private val lifecycleEvents = mutableListOf<Lifecycle.Event>()
    private val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            lifecycleEvents += event
        }
    }

    private val lifecycleOwner = ConstrainedStateLifecycleOwner.createUnsafe(parentLifecycleOwner)

    @Test
    fun verifyMaxStateIsObserved() {
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED

        lifecycleOwner.maxState = Lifecycle.State.CREATED
        assertEquals(Lifecycle.State.CREATED, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        assertEquals(Lifecycle.State.STARTED, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.maxState = Lifecycle.State.RESUMED
        assertEquals(Lifecycle.State.RESUMED, lifecycleOwner.lifecycle.currentState)
    }

    @Test
    fun verifyUpPastMaxStateIsConstrained() {
        parentLifecycleOwner.currentState = Lifecycle.State.CREATED
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        lifecycleOwner.lifecycle.addObserver(observer)
        lifecycleEvents.clear()

        assertEquals(Lifecycle.State.CREATED, lifecycleOwner.lifecycle.currentState)
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        assertEquals(Lifecycle.State.STARTED, lifecycleOwner.lifecycle.currentState)
        assertContentEquals(listOf(Lifecycle.Event.ON_START), lifecycleEvents)
    }

    @Test
    fun verifyDownBelowMaxStateDoesntWork() {
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        lifecycleOwner.lifecycle.addObserver(observer)
        lifecycleEvents.clear()

        parentLifecycleOwner.currentState = Lifecycle.State.CREATED
        assertEquals(Lifecycle.State.CREATED, lifecycleOwner.lifecycle.currentState)
        assertContentEquals(listOf(Lifecycle.Event.ON_STOP), lifecycleEvents)
    }

    @Test
    fun verifyChangingMaxStateTriggersEvents() {
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        lifecycleOwner.lifecycle.addObserver(observer)
        lifecycleEvents.clear()

        assertEquals(Lifecycle.State.RESUMED, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        assertContentEquals(listOf(Lifecycle.Event.ON_PAUSE), lifecycleEvents)
    }
}
