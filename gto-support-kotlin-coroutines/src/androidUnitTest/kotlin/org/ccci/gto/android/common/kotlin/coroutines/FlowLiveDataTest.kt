package org.ccci.gto.android.common.kotlin.coroutines

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowLiveDataTest {
    private val observerValues = mutableListOf<Any?>()
    private val observer = mockk<Observer<Any?>> {
        every { onChanged(captureNullable(observerValues)) } returns Unit
    }

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `collectInto - Normal usage`() = runTest {
        val flow = flowOf("a", "b", "c").onEach { delay(1) }
        val target = MutableLiveData<Any>()
        target.observeForever(observer)
        flow.collectInto(target)
        assertEquals("c", target.value)
        assertThat(observerValues, contains("a", "b", "c"))
    }
}
