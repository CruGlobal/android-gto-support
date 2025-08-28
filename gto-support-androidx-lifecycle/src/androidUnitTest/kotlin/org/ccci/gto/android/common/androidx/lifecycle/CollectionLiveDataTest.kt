package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.JunitTaskExecutorRule
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

abstract class CollectionLiveDataTest(private val synchronous: Boolean) {
    @get:Rule
    val rule = JunitTaskExecutorRule(1, false)

    abstract val liveData: CollectionLiveData<String, out Collection<String>>
    lateinit var observer: Observer<Any?>

    @Before
    fun setupLiveData() {
        observer = mock()
        executeTask(mainThread = true) { liveData.observeForever(observer) }
        clearInvocations(observer)
    }

    @Test
    fun testAdd() {
        executeTask { assertTrue(liveData.add("a")) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testAddAll() {
        executeTask { assertTrue(liveData.addAll(setOf("a", "b", "c"))) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a", "b", "c"))
    }

    @Test
    fun testAddAllEmpty() {
        executeTask { assertFalse(liveData.addAll(emptySet())) }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, empty())
    }

    @Test
    fun testPlusAssign() {
        executeTask { liveData += "a" }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testPlusAssignCollection() {
        executeTask { liveData += setOf("a", "b", "c") }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a", "b", "c"))
    }

    @Test
    fun testPlusAssignCollectionEmpty() {
        executeTask { liveData += emptySet() }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, empty())
    }

    @Test
    fun testRemove() {
        executeTask { liveData += setOf("a", "b") }
        clearInvocations(observer)

        executeTask { assertTrue(liveData.remove("a")) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("b"))
    }

    @Test
    fun testRemoveMissing() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { assertFalse(liveData.remove("b")) }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testRemoveAll() {
        executeTask { liveData += setOf("a", "b") }
        clearInvocations(observer)

        executeTask { assertTrue(liveData.removeAll(setOf("b", "c"))) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testRemoveAllNoChange() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { assertFalse(liveData.removeAll(setOf("b", "c"))) }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testRemoveAllPredicate() {
        executeTask { liveData += setOf("a", "bb", "c") }
        clearInvocations(observer)

        executeTask { assertTrue(liveData.removeAll { it.length < 2 }) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("bb"))
        clearInvocations(observer)

        executeTask { assertFalse(liveData.removeAll { it.length < 2 }) }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("bb"))
    }

    @Test
    fun testRetainAll() {
        executeTask { liveData += setOf("a", "b") }
        clearInvocations(observer)

        executeTask { assertTrue(liveData.retainAll(setOf("b", "c"))) }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("b"))
    }

    @Test
    fun testRetainAllNoChange() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { assertFalse(liveData.retainAll(setOf("a", "b"))) }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssign() {
        executeTask { liveData += setOf("a", "b") }
        clearInvocations(observer)

        executeTask { liveData -= "a" }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("b"))
    }

    @Test
    fun testMinusAssignMissing() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { liveData -= "b" }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssignCollection() {
        executeTask { liveData += setOf("a", "b") }
        clearInvocations(observer)

        executeTask { liveData -= setOf("b", "c") }
        verify(observer).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssignCollectionMissing() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { liveData -= setOf("b", "c") }
        verify(observer, never()).onChanged(any())
        assertThat(liveData.value, containsInAnyOrder("a"))
    }

    @Test
    fun testClear() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { liveData.clear() }
        verify(observer).onChanged(any())
        assertThat(liveData.value, empty())
    }

    @Test
    fun testClearEmpty() {
        executeTask { liveData.clear() }
        verify(observer).onChanged(any())
        assertThat(liveData.value, empty())
    }

    @Test
    fun testAsynchronous() {
        // HACK: we can't use "assumeFalse(synchronous)" because JunitTaskExecutorRule wraps AssumptionViolatedException
        if (synchronous) return

        // stall the main thread while we run the background thread update
        val latch = CountDownLatch(1)
        executeTask(mainThread = true, awaitExecution = false) { latch.await() }

        // asynchronous CollectionLiveData can update the LiveData on a background thread.
        // observer updates are still triggered from the main thread at a later time
        executeTask(mainThread = false) {
            liveData += "a"
            verify(observer, never()).onChanged(any())

            // allow the main thread to proceed
            latch.countDown()
        }
        verify(observer).onChanged(any())
    }

    @Test
    fun testSynchronous() {
        // HACK: we can't use "assumeTrue(synchronous)" because JunitTaskExecutorRule wraps AssumptionViolatedException
        if (!synchronous) return

        executeTask(mainThread = true) {
            liveData += "a"
            verify(observer).onChanged(any())
        }
    }

    @Test
    fun testSynchronousFromBackgroundThread() {
        // HACK: we can't use "assumeTrue(synchronous)" because JunitTaskExecutorRule wraps AssumptionViolatedException
        if (!synchronous) return

        // updating fails on a background thread
        executeTask(mainThread = false) {
            try {
                liveData += "b"
                fail("You shouldn't be able to update the LiveData from a background thread when using synchronous")
            } catch (e: IllegalStateException) {
            }
        }
    }

    private fun executeTask(mainThread: Boolean = synchronous, awaitExecution: Boolean = true, block: () -> Unit) {
        try {
            if (mainThread) {
                rule.taskExecutor.executeOnMainThread(block)
            } else {
                rule.taskExecutor.executeOnDiskIO(block)
            }
        } finally {
            if (awaitExecution) {
                rule.drainTasks(1)
                // drain one additional time for asynchronous because LiveData.postValue queues up another task
                if (!synchronous) rule.drainTasks(1)
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "synchronous = {0}")
        fun parameters() = listOf(arrayOf(true), arrayOf(false))
    }
}

@RunWith(Parameterized::class)
class ListLiveDataTest(synchronous: Boolean) : CollectionLiveDataTest(synchronous) {
    override val liveData = ListLiveData<String>(synchronous)
}

@RunWith(Parameterized::class)
class SetLiveDataTest(synchronous: Boolean) : CollectionLiveDataTest(synchronous) {
    override val liveData = SetLiveData<String>(synchronous)
}
