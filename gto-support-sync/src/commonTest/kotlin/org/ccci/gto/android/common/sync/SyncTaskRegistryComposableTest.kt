package org.ccci.gto.android.common.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlinx.coroutines.test.TestScope
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith

@OptIn(ExperimentalTestApi::class)
@RunOnAndroidWith(AndroidJUnit4::class)
class SyncTaskRegistryComposableTest {
    private val testScope = TestScope()
    private val syncTracker = SyncTracker(testScope.backgroundScope)

    // region rememberSyncTaskRegistry()
    @Test
    fun `rememberSyncTaskRegistry - wraps provided syncTracker`() = runComposeUiTest {
        lateinit var registry: SyncTaskRegistry
        setContent { registry = rememberSyncTaskRegistry(syncTracker) }
        assertSame(syncTracker, registry.syncTracker)
    }

    @Test
    fun `rememberSyncTaskRegistry - stable across recompositions`() = runComposeUiTest {
        val instances = mutableListOf<SyncTaskRegistry>()
        var trigger by mutableIntStateOf(0)

        setContent {
            trigger
            instances += rememberSyncTaskRegistry(syncTracker)
        }

        trigger++
        waitForIdle()

        assertEquals(2, instances.size)
        assertSame(instances[0], instances[1])
    }
    // endregion rememberSyncTaskRegistry()

    // region SyncTaskRegistry.rememberSyncTask()
    @Test
    fun `rememberSyncTask - task is registered on composition`() = runComposeUiTest {
        val registry = SyncTaskRegistry(syncTracker)
        var taskId: String? = null
        val invocations = mutableListOf<Boolean>()

        setContent { taskId = registry.rememberSyncTask { force -> invocations.add(force) } }
        invocations.clear()

        registry.triggerSyncTasks()

        assertNotNull(taskId)
        assertEquals(listOf(false), invocations)
    }

    @Test
    fun `rememberSyncTask - task is unregistered on dispose`() = runComposeUiTest {
        val registry = SyncTaskRegistry(syncTracker)
        val invocations = mutableListOf<Boolean>()
        var active by mutableStateOf(true)

        setContent {
            if (active) registry.rememberSyncTask { force -> invocations.add(force) }
        }
        invocations.clear()

        active = false
        waitForIdle()

        registry.triggerSyncTasks()

        assertEquals(emptyList(), invocations)
    }

    @Test
    fun `rememberSyncTask - stable across recompositions - no key`() = runComposeUiTest {
        val registry = SyncTaskRegistry(syncTracker)
        var trigger by mutableIntStateOf(0)
        var taskId: String? = null

        setContent {
            trigger
            taskId = registry.rememberSyncTask { }
        }
        val initialId = taskId

        trigger++
        waitForIdle()

        assertNotNull(taskId)
        assertEquals(initialId, taskId)
    }

    @Test
    fun `rememberSyncTask - stable across recompositions - single key`() = runComposeUiTest {
        val registry = SyncTaskRegistry(syncTracker)
        var trigger by mutableIntStateOf(0)
        var taskId: String? = null

        setContent {
            trigger
            taskId = registry.rememberSyncTask("key") { }
        }
        val initialId = taskId

        trigger++
        waitForIdle()

        assertNotNull(taskId)
        assertEquals(initialId, taskId)
    }

    @Test
    fun `rememberSyncTask - re-registers task when key changes`() = runComposeUiTest {
        val registry = SyncTaskRegistry(syncTracker)
        var taskId: String? = null
        var key by mutableStateOf("initial")
        val invocations = mutableListOf<Boolean>()

        setContent { taskId = registry.rememberSyncTask(key) { force -> invocations.add(force) } }
        val firstId = taskId
        invocations.clear()

        key = "changed"
        waitForIdle()
        invocations.clear()

        registry.triggerSyncTasks()

        assertNotNull(taskId)
        assertNotEquals(firstId, taskId)
        assertEquals(listOf(false), invocations)
    }
    // endregion SyncTaskRegistry.rememberSyncTask()
}
