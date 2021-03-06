package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConstrainedStateLifecycleOwnerTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val parentLifecycleOwner = TestLifecycleOwner(Lifecycle.State.STARTED, TestCoroutineDispatcher())
    private lateinit var observer: DefaultLifecycleObserver

    private lateinit var lifecycleOwner: ConstrainedStateLifecycleOwner

    @Before
    fun setup() {
        parentLifecycleOwner.currentState = Lifecycle.State.CREATED
        lifecycleOwner = ConstrainedStateLifecycleOwner(parentLifecycleOwner)
        observer = mock()
    }

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
        clearInvocations(observer)

        assertEquals(Lifecycle.State.CREATED, lifecycleOwner.lifecycle.currentState)
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        assertEquals(Lifecycle.State.STARTED, lifecycleOwner.lifecycle.currentState)
        inOrder(observer) {
            verify(observer).onStart(any())
            verify(observer, never()).onResume(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun verifyDownBelowMaxStateDoesntWork() {
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        lifecycleOwner.lifecycle.addObserver(observer)
        clearInvocations(observer)

        parentLifecycleOwner.currentState = Lifecycle.State.CREATED
        assertEquals(Lifecycle.State.CREATED, lifecycleOwner.lifecycle.currentState)
        inOrder(observer) {
            verify(observer, never()).onPause(any())
            verify(observer).onStop(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun verifyChangingMaxStateTriggersEvents() {
        parentLifecycleOwner.currentState = Lifecycle.State.RESUMED
        lifecycleOwner.lifecycle.addObserver(observer)
        clearInvocations(observer)

        assertEquals(Lifecycle.State.RESUMED, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.maxState = Lifecycle.State.STARTED
        inOrder(observer) {
            verify(observer).onPause(any())
            verifyNoMoreInteractions()
        }
    }
}
