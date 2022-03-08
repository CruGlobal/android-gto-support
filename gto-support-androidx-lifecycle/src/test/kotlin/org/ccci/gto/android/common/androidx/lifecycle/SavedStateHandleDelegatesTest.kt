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
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val PROPERTY = "property"

@OptIn(ExperimentalCoroutinesApi::class)
class SavedStateHandleDelegatesTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val scope = TestScope(StandardTestDispatcher())

    private val savedState = SavedStateHandle()
    private val delegates = object {
        var property: String? by savedState.delegate()
        var propertyOther: String? by savedState.delegate(PROPERTY)
        var propertyNotNull: String by savedState.delegate(PROPERTY, ifNull = "default")
        val livedata by savedState.livedata<String>(PROPERTY)
        val stateFlow by savedState.stateFlow<String>(scope, PROPERTY)
        val stateFlowInitial by savedState.stateFlow(scope, PROPERTY, "initial")
    }

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun cleanup() {
        scope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `Property Delegate`() {
        delegates.property = "test"
        assertEquals("test", savedState[PROPERTY])
        assertEquals("test", delegates.property)
        assertEquals("test", delegates.propertyOther)
        assertEquals("test", delegates.propertyNotNull)
        assertEquals("test", delegates.livedata.value)
        assertEquals("test", delegates.stateFlow.value)
        assertEquals("test", delegates.stateFlowInitial.value)
    }

    @Test
    fun `Property Delegate - Other Key`() {
        delegates.propertyOther = "test"
        assertEquals("test", savedState[PROPERTY])
        assertEquals("test", delegates.property)
        assertEquals("test", delegates.propertyOther)
        assertEquals("test", delegates.propertyNotNull)
        assertEquals("test", delegates.livedata.value)
        assertEquals("test", delegates.stateFlow.value)
        assertEquals("test", delegates.stateFlowInitial.value)
    }

    @Test
    fun `Property Delegate - Null Value`() {
        delegates.property = null
        assertNull(delegates.property)
        assertNull(delegates.propertyOther)
        assertEquals("default", delegates.propertyNotNull)
        assertNull(delegates.stateFlow.value)
    }

    @Test
    fun `LiveData Delegate`() {
        delegates.livedata.value = "livedata"
        assertEquals("livedata", savedState[PROPERTY])
        assertEquals("livedata", delegates.property)
        assertEquals("livedata", delegates.livedata.value)
        assertEquals("livedata", delegates.stateFlow.value)
        assertEquals("livedata", delegates.stateFlowInitial.value)
    }

    @Test
    fun `StateFlow Delegate`() {
        delegates.stateFlow.value = "stateFlow"
        assertEquals("stateFlow", savedState[PROPERTY])
        assertEquals("stateFlow", delegates.property)
        assertEquals("stateFlow", delegates.livedata.value)
        assertEquals("stateFlow", delegates.stateFlow.value)
        assertEquals("stateFlow", delegates.stateFlowInitial.value)
    }
}
