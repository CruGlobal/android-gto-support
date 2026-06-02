package org.ccci.gto.android.common.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.InternalCircuitApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlinx.coroutines.test.TestScope
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith

@OptIn(ExperimentalTestApi::class, InternalCircuitApi::class)
@RunOnAndroidWith(AndroidJUnit4::class)
class SyncTaskRegistryCircuitTest {
    private val testScope = TestScope()
    private val syncTracker = SyncTracker(testScope.backgroundScope)

    // region CircuitContext.rememberSyncTaskRegistry()
    @Test
    fun `rememberSyncTaskRegistry - wraps provided syncTracker`() = runComposeUiTest {
        val context = CircuitContext(null)
        lateinit var registry: SyncTaskRegistry

        setContent { registry = context.rememberSyncTaskRegistry(syncTracker) }

        assertSame(syncTracker, registry.syncTracker)
    }

    @Test
    fun `rememberSyncTaskRegistry - sets registry on context`() = runComposeUiTest {
        val context = CircuitContext(null)

        setContent { context.rememberSyncTaskRegistry(syncTracker) }

        assertNotNull(context.syncTaskRegistry)
    }

    @Test
    fun `rememberSyncTaskRegistry - clears registry from context on dispose`() = runComposeUiTest {
        val context = CircuitContext(null)
        var active by mutableStateOf(true)

        setContent {
            if (active) context.rememberSyncTaskRegistry(syncTracker)
        }

        active = false
        waitForIdle()

        assertNull(context.syncTaskRegistry)
    }

    @Test
    fun `rememberSyncTaskRegistry - stable across recompositions`() = runComposeUiTest {
        val context = CircuitContext(null)
        val instances = mutableListOf<SyncTaskRegistry>()
        var trigger by mutableIntStateOf(0)

        setContent {
            trigger
            instances += context.rememberSyncTaskRegistry(syncTracker)
        }

        trigger++
        waitForIdle()

        assertEquals(2, instances.size)
        assertSame(instances[0], instances[1])
    }
    // endregion CircuitContext.rememberSyncTaskRegistry()

    // region CircuitContext.rememberSyncTask()
    @Test
    fun `rememberSyncTask - registers task through context registry`() = runComposeUiTest {
        val context = CircuitContext(null)
        val registry = SyncTaskRegistry(syncTracker)
        context.syncTaskRegistry = registry
        val invocations = mutableListOf<Boolean>()

        setContent { context.rememberSyncTask { force -> invocations.add(force) } }
        invocations.clear()

        registry.triggerSyncTasks()

        assertEquals(listOf(false), invocations)
    }

    @Test
    fun `rememberSyncTask - unregisters task on dispose`() = runComposeUiTest {
        val context = CircuitContext(null)
        val registry = SyncTaskRegistry(syncTracker)
        context.syncTaskRegistry = registry
        val invocations = mutableListOf<Boolean>()
        var taskActive by mutableStateOf(true)

        setContent {
            if (taskActive) context.rememberSyncTask { force -> invocations.add(force) }
        }
        invocations.clear()

        taskActive = false
        waitForIdle()

        registry.triggerSyncTasks()

        assertEquals(emptyList(), invocations)
    }

    @Test
    fun `rememberSyncTask - returns null when context has no registry`() = runComposeUiTest {
        val context = CircuitContext(null)
        var taskId: String? = "initial"

        setContent { taskId = context.rememberSyncTask { } }

        assertNull(taskId)
    }

    @Test
    fun `rememberSyncTask - finds registry from parent context`() = runComposeUiTest {
        val parentContext = CircuitContext(null)
        val childContext = CircuitContext(parentContext)
        val registry = SyncTaskRegistry(syncTracker)
        parentContext.syncTaskRegistry = registry
        val invocations = mutableListOf<Boolean>()

        setContent { childContext.rememberSyncTask { force -> invocations.add(force) } }
        invocations.clear()

        registry.triggerSyncTasks()

        assertEquals(listOf(false), invocations)
    }
    // endregion CircuitContext.rememberSyncTask()
}
