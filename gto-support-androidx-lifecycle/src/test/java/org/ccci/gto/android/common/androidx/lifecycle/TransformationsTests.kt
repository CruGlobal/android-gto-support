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

    private val str1 = MutableLiveData("a")
    private val str2 = MutableLiveData<String?>(null)
    private val str3 = MutableLiveData<String?>(null)

    @Test
    fun verifySwitchCombineWith() {
        val combined = str1.switchCombineWith(str2) { a, b ->
            when {
                a == null || b == null -> emptyLiveData<String>()
                else -> MutableLiveData(a + b)
            }
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever { }

        assertNull(combined.value)
        str1.value = "b"
        assertNull(combined.value)
        str1.value = "a"
        str2.value = "b"
        assertEquals("ab", combined.value)
    }

    @Test
    fun verifyCombineWith2() {
        val combined = str1.combineWith(str2) { a, b ->
            when {
                a == null || b == null -> null
                else -> a + b
            }
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever { }

        assertNull(combined.value)
        str1.value = "b"
        assertNull(combined.value)
        str1.value = "a"
        str2.value = "b"
        assertEquals("ab", combined.value)
    }

    @Test
    fun verifyCombineWith3() {
        val combined = str1.combineWith(str2, str3) { a, b, c ->
            listOfNotNull(a, b, c).joinToString()
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever { }

        assertEquals("a", combined.value)
        str1.value = "b"
        assertEquals("b", combined.value)
        str3.value = "d"
        assertEquals("b, d", combined.value)
        str2.value = "c"
        assertEquals("b, c, d", combined.value)
    }
}
