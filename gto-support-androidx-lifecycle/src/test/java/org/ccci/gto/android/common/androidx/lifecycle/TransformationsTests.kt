package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransformationsTests {
    private lateinit var observer: Observer<Any>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val str1 = MutableLiveData("a")
    private val str2 = MutableLiveData<String?>()
    private val str3 = MutableLiveData<String?>()

    @Before
    fun setup() {
        observer = mock()
    }

    @Test
    fun verifySwitchCombineWith() {
        val combined = str1.switchCombineWith(str2) { a, b ->
            MutableLiveData(listOfNotNull(a, b).joinToString())
        }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever(observer)

        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str1.value = "b"
        verify(observer, never()).onChanged(any())
        assertNull(combined.value)
        str2.value = "c"
        verify(observer).onChanged(any())
        assertEquals("b, c", combined.value)
    }

    @Test
    fun verifyCombineWith2() {
        val combined = str1.combineWith(str2) { a, b -> listOfNotNull(a, b).joinToString() }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever(observer)

        assertEquals("a", combined.value)
        str1.value = "b"
        assertEquals("b", combined.value)
        str2.value = "c"
        assertEquals("b, c", combined.value)
    }

    @Test
    fun verifyCombineWith3() {
        val combined = str1.combineWith(str2, str3) { a, b, c -> listOfNotNull(a, b, c).joinToString() }
        // observeForever activates the internal MediatorLiveData
        combined.observeForever(observer)

        assertEquals("a", combined.value)
        str1.value = "b"
        assertEquals("b", combined.value)
        str3.value = "d"
        assertEquals("b, d", combined.value)
        str2.value = "c"
        assertEquals("b, c, d", combined.value)
    }
}
