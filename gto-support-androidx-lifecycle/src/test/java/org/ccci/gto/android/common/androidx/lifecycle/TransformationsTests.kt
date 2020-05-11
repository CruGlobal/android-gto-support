package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class TransformationsTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val str = MutableLiveData("a")
    private val strNullable = MutableLiveData<String?>(null)

    @Test
    fun verifySwitchCombineWith() {
        val combined = str.switchCombineWith(strNullable) { a, b ->
            when {
                a == null || b == null -> emptyLiveData<String>()
                else -> MutableLiveData(a + b)
            }
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever { }

        assertNull(combined.value)
        str.value = "b"
        assertNull(combined.value)
        str.value = "a"
        strNullable.value = "b"
        assertEquals("ab", combined.value)
    }

    @Test
    fun verifyCombineWith() {
        val combined = str.combineWith(strNullable) { a, b ->
            when {
                a == null || b == null -> null
                else -> a + b
            }
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever { }

        assertNull(combined.value)
        str.value = "b"
        assertNull(combined.value)
        str.value = "a"
        strNullable.value = "b"
        assertEquals("ab", combined.value)
    }
}
