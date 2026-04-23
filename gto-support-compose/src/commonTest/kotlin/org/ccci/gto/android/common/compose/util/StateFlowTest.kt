package org.ccci.gto.android.common.compose.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlinx.coroutines.flow.StateFlow
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith

@OptIn(ExperimentalTestApi::class)
@RunOnAndroidWith(AndroidJUnit4::class)
class StateFlowTest {
    @Test
    fun `rememberStateFlow - initial value is correct`() = runComposeUiTest {
        lateinit var flow: StateFlow<String>
        setContent { flow = rememberStateFlow("hello") }

        assertEquals("hello", flow.value)
    }

    @Test
    fun `rememberStateFlow - emits updated values on recomposition`() = runComposeUiTest {
        var input by mutableStateOf("initial")
        lateinit var flow: StateFlow<String>
        setContent { flow = rememberStateFlow(input) }

        flow.test {
            assertEquals("initial", awaitItem())

            input = "updated"
            waitForIdle()
            assertEquals("updated", awaitItem())
        }
    }

    @Test
    fun `rememberStateFlow - StateFlow reference is stable across recompositions`() = runComposeUiTest {
        var input by mutableStateOf("initial")
        val instances = mutableListOf<StateFlow<String>>()
        setContent { instances += rememberStateFlow(input) }

        input = "updated"
        waitForIdle()

        assertEquals(2, instances.size, "Expected two StateFlow instances across two compositions")
        instances.forEach {
            assertSame(instances[0], it, "Expected StateFlow instance to be stable across recompositions")
        }
    }
}
