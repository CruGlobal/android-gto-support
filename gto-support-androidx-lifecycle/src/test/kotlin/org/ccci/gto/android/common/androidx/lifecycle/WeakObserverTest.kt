package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeakObserverTest : BaseLiveDataTest() {
    var counter = 0
    val liveData = MutableLiveData(counter)

    @Test
    fun `remove observer manually`() {
        val obj = Any()
        assertFalse(liveData.hasActiveObservers())
        val observer = liveData.observeWeak(lifecycleOwner, obj) { counter++ }
        assertTrue(liveData.hasActiveObservers())
        assertEquals(1, counter)

        liveData.value = counter
        assertEquals(2, counter)

        liveData.removeObserver(observer)
        liveData.value = counter
        assertFalse(liveData.hasActiveObservers())
        assertEquals(2, counter)
    }

    @Test
    fun `remove observer automatically when obj is garbage collected`() {
        var obj = Any()
        assertFalse(liveData.hasActiveObservers())
        liveData.observeWeak(lifecycleOwner, obj) { counter++ }
        assertTrue(liveData.hasActiveObservers())
        assertEquals(1, counter)

        liveData.value = counter
        assertEquals(2, counter)

        obj = Any()
        System.gc()
        liveData.value = counter
        assertFalse(liveData.hasActiveObservers())
        assertEquals(2, counter)
    }

    @Test
    fun `remove observer automatically when lifecycle is destroyed`() {
        val obj = Any()
        assertFalse(liveData.hasActiveObservers())
        liveData.observeWeak(lifecycleOwner, obj) { counter++ }
        assertTrue(liveData.hasActiveObservers())
        assertEquals(1, counter)

        liveData.value = counter
        assertEquals(2, counter)

        lifecycleOwner.currentState = Lifecycle.State.DESTROYED
        liveData.value = counter
        assertFalse(liveData.hasActiveObservers())
        assertEquals(2, counter)
    }
}
