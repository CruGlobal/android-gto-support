package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val KEY = "key"

@OptIn(ExperimentalCoroutinesApi::class)
class SavedStateHandleStateFlowTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMutableStateFlow() - updating value`() = runTest {
        val flowScope = TestScope(StandardTestDispatcher())
        assertNull(savedStateHandle[KEY])

        val liveData = savedStateHandle.getLiveData<String?>(KEY)
        val stateFlow = savedStateHandle.getMutableStateFlow<String?>(flowScope, KEY, "initial")
        assertEquals("initial", stateFlow.value)

        // update key directly
        savedStateHandle[KEY] = "direct"
        assertEquals("direct", stateFlow.value)
        assertEquals("direct", liveData.value)
        assertEquals("direct", savedStateHandle[KEY])

        // update livedata
        savedStateHandle.getLiveData<String?>(KEY).value = "livedata"
        assertEquals("livedata", stateFlow.value)
        assertEquals("livedata", liveData.value)
        assertEquals("livedata", savedStateHandle[KEY])

        // update flow
        stateFlow.value = "flow"
        assertEquals("flow", stateFlow.value)
        assertEquals("flow", liveData.value)
        assertEquals("flow", savedStateHandle[KEY])

        flowScope.cancel()
    }

    @Test
    fun `getMutableStateFlow() - uses MainDispatcher for syncing updates`() = runTest {
        val mainDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(mainDispatcher)

        val flowScope = TestScope(StandardTestDispatcher())
        assertNull(savedStateHandle[KEY])

        val liveData = savedStateHandle.getLiveData<String?>(KEY)
        val stateFlow = savedStateHandle.getMutableStateFlow<String?>(flowScope, KEY, "initial")
        assertEquals("initial", stateFlow.value)

        // update key directly
        savedStateHandle[KEY] = "direct"
        assertEquals("direct", stateFlow.value)
        assertEquals("direct", liveData.value)
        assertEquals("direct", savedStateHandle[KEY])

        // update livedata
        savedStateHandle.getLiveData<String?>(KEY).value = "livedata"
        assertEquals("livedata", stateFlow.value)
        assertEquals("livedata", liveData.value)
        assertEquals("livedata", savedStateHandle[KEY])

        // update flow
        stateFlow.value = "flow"
        assertEquals("flow", stateFlow.value)
        assertNotEquals("flow", liveData.value)
        assertNotEquals("flow", savedStateHandle[KEY])
        runCurrent()
        assertEquals("flow", liveData.value)
        assertEquals("flow", savedStateHandle[KEY])

        flowScope.cancel()
    }

    @Test
    fun `getMutableStateFlow() - don't overwrite existing value`() = runTest {
        val flowScope = TestScope(StandardTestDispatcher())
        savedStateHandle[KEY] = "already set"

        val stateFlow = savedStateHandle.getMutableStateFlow<String?>(flowScope, KEY, "initial")
        assertEquals("already set", stateFlow.value)

        flowScope.cancel()
    }
}
