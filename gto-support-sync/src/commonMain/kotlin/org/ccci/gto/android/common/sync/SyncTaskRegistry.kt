package org.ccci.gto.android.common.sync

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalAtomicApi::class)
class SyncTaskRegistry(val syncTracker: SyncTracker) {
    private val tasks = AtomicReference(mapOf<String, SyncTracker.(force: Boolean) -> Unit>())

    @OptIn(ExperimentalUuidApi::class)
    fun registerSyncTask(task: SyncTracker.(force: Boolean) -> Unit): String {
        val id = Uuid.generateV7().toString()
        tasks.update { it + (id to task) }
        syncTracker.task(false)
        return id
    }

    fun unregisterSyncTask(id: String) {
        tasks.update { it - id }
    }

    fun triggerSyncTasks(force: Boolean = false) {
        tasks.load().values.forEach { syncTracker.it(force) }
    }
}
