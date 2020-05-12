package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransformationsWithInitialValueTest {
    private lateinit var observer: Observer<Any>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        observer = mock()
    }

    @Test
    fun verifyWithInitialValue() {
        val source = MutableLiveData<String>()

        val liveData = source.withInitialValue("a")
        liveData.observeForever(observer)
        verify(observer).onChanged(any())
        assertEquals("a", liveData.value)
        source.value = "a"
        assertEquals("a", liveData.value)
        source.value = "b"
        assertEquals("b", liveData.value)

        argumentCaptor<String> {
            verify(observer, times(3)).onChanged(capture())
            assertThat(allValues, contains("a", "a", "b"))
        }
    }

    @Test
    fun verifyWithInitialValueWhenSourceIsAlreadyInitialized() {
        val source = MutableLiveData<String>("b")

        val liveData = source.withInitialValue("a")
        liveData.observeForever(observer)
        verify(observer).onChanged(any())
        assertEquals("b", liveData.value)
        source.value = "c"
        assertEquals("c", liveData.value)

        argumentCaptor<String> {
            verify(observer, times(2)).onChanged(capture())
            assertThat(allValues, contains("b", "c"))
        }
    }
}
