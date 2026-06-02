package org.ccci.gto.android.common.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlinx.coroutines.test.TestScope

class SyncTaskRegistryTest {
    private val testScope = TestScope()
    private val syncTracker = SyncTracker(testScope.backgroundScope)
    private val registry = SyncTaskRegistry(syncTracker)

    // region registerSyncTask()
    @Test
    fun `registerSyncTask - triggers task immediately with force=false`() {
        val invocations = mutableListOf<Boolean>()
        registry.registerSyncTask { force -> invocations.add(force) }
        assertEquals(listOf(false), invocations)
    }

    @Test
    fun `registerSyncTask - returns unique IDs`() {
        val id1 = registry.registerSyncTask { }
        val id2 = registry.registerSyncTask { }
        assertNotEquals(id1, id2)
    }
    // endregion registerSyncTask()

    // region unregisterSyncTask()
    @Test
    fun `unregisterSyncTask - task is not called after unregistration`() {
        val invocations = mutableListOf<Boolean>()
        val id = registry.registerSyncTask { force -> invocations.add(force) }
        invocations.clear()

        registry.unregisterSyncTask(id)
        registry.triggerSyncTasks()

        assertEquals(emptyList(), invocations)
    }

    @Test
    fun `unregisterSyncTask - only removes the specified task`() {
        val invocations1 = mutableListOf<Boolean>()
        val invocations2 = mutableListOf<Boolean>()
        val id1 = registry.registerSyncTask { force -> invocations1.add(force) }
        registry.registerSyncTask { force -> invocations2.add(force) }
        invocations1.clear()
        invocations2.clear()

        registry.unregisterSyncTask(id1)
        registry.triggerSyncTasks()

        assertEquals(emptyList(), invocations1)
        assertEquals(listOf(false), invocations2)
    }
    // endregion unregisterSyncTask()

    // region triggerSyncTasks()
    @Test
    fun `triggerSyncTasks - triggers all registered tasks`() {
        val invocations1 = mutableListOf<Boolean>()
        val invocations2 = mutableListOf<Boolean>()
        registry.registerSyncTask { force -> invocations1.add(force) }
        registry.registerSyncTask { force -> invocations2.add(force) }
        invocations1.clear()
        invocations2.clear()

        registry.triggerSyncTasks()

        assertEquals(listOf(false), invocations1)
        assertEquals(listOf(false), invocations2)
    }

    @Test
    fun `triggerSyncTasks - passes force value to all tasks`() {
        val invocations = mutableListOf<Boolean>()
        registry.registerSyncTask { force -> invocations.add(force) }
        invocations.clear()

        registry.triggerSyncTasks(force = true)

        assertEquals(listOf(true), invocations)
    }
    // endregion triggerSyncTasks()
}
