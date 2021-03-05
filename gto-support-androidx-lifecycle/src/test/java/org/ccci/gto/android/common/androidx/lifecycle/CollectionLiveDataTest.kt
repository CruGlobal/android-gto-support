package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.JunitTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class CollectionLiveDataTest {
    @get:Rule
    val rule = JunitTaskExecutorRule(1, false)

    abstract val liveData: CollectionLiveData<String, out Collection<String>>
    lateinit var observer: Observer<Any?>

    @Before
    fun setupLiveData() {
        observer = mock()
        executeTask(mainThread = true, awaitExecution = true) { liveData.observeForever(observer) }
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
    fun testRemoveAllMissing() {
        executeTask { liveData += setOf("a") }
        clearInvocations(observer)

        executeTask { assertFalse(liveData.removeAll(setOf("b", "c"))) }
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

    private fun executeTask(mainThread: Boolean = false, awaitExecution: Boolean = true, block: () -> Unit) {
        try {
            if (mainThread) {
                rule.taskExecutor.executeOnMainThread(block)
            } else {
                rule.taskExecutor.executeOnDiskIO(block)
            }
        } finally {
            if (awaitExecution) {
                rule.drainTasks(1)
                rule.drainTasks(1)
            }
        }
    }
}

class ListLiveDataTest : CollectionLiveDataTest() {
    override val liveData = ListLiveData<String>()
}

class SetLiveDataTest : CollectionLiveDataTest() {
    override val liveData = SetLiveData<String>()
}
