package org.ccci.gto.android.common.lifecycle.databinding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.lifecycle.CollectionLiveData
import org.ccci.gto.android.common.lifecycle.ListLiveData
import org.ccci.gto.android.common.lifecycle.SetLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class CollectionLiveDataTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    abstract val liveData: CollectionLiveData<String, out Collection<String>, *>
    lateinit var observer: Observer<Any?>

    @Before
    fun setupObserver() {
        observer = mock()
        liveData.observeForever(observer)
        reset(observer)
    }

    @Test
    fun testAdd() {
        assertTrue(liveData.add("a"))
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testAddAll() {
        assertTrue(liveData.addAll(setOf("a", "b", "c")))
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a", "b", "c"))
    }

    @Test
    fun testAddAllEmpty() {
        assertFalse(liveData.addAll(emptySet()))
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), empty())
    }

    @Test
    fun testPlusAssign() {
        liveData += "a"
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testPlusAssignCollection() {
        liveData += setOf("a", "b", "c")
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a", "b", "c"))
    }

    @Test
    fun testPlusAssignCollectionEmpty() {
        liveData += emptySet()
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), empty())
    }

    @Test
    fun testRemove() {
        liveData += setOf("a", "b")
        reset(observer)

        assertTrue(liveData.remove("a"))
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("b"))
    }

    @Test
    fun testRemoveMissing() {
        liveData += setOf("a")
        reset(observer)

        assertFalse(liveData.remove("b"))
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testRemoveAll() {
        liveData += setOf("a", "b")
        reset(observer)

        assertTrue(liveData.removeAll(setOf("b", "c")))
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testRemoveAllMissing() {
        liveData += setOf("a")
        reset(observer)

        assertFalse(liveData.removeAll(setOf("b", "c")))
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssign() {
        liveData += setOf("a", "b")
        reset(observer)

        liveData -= "a"
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("b"))
    }

    @Test
    fun testMinusAssignMissing() {
        liveData += setOf("a")
        reset(observer)

        liveData -= "b"
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssignCollection() {
        liveData += setOf("a", "b")
        reset(observer)

        liveData -= setOf("b", "c")
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testMinusAssignCollectionMissing() {
        liveData += setOf("a")
        reset(observer)

        liveData -= setOf("b", "c")
        verify(observer, never()).onChanged(any())
        assertThat(liveData.getValue(), containsInAnyOrder("a"))
    }

    @Test
    fun testClear() {
        liveData += setOf("a")
        reset(observer)

        liveData.clear()
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), empty())
    }

    @Test
    fun testClearEmpty() {
        liveData.clear()
        verify(observer).onChanged(any())
        assertThat(liveData.getValue(), empty())
    }
}

class ListLiveDataTests : CollectionLiveDataTests() {
    override val liveData = ListLiveData<String>()
}

class SetLiveDataTests : CollectionLiveDataTests() {
    override val liveData = SetLiveData<String>()
}
