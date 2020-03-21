package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
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
        val combined: LiveData<String?> = str.switchCombineWith(strNullable) { a, b ->
            when {
                a == null || b == null -> return@switchCombineWith emptyLiveData<String>()
                else -> return@switchCombineWith MutableLiveData(a + b) as LiveData<String>
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
