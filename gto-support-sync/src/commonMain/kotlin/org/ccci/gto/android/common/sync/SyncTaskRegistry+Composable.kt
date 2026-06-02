package org.ccci.gto.android.common.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot

@Composable
fun rememberSyncTaskRegistry(syncTracker: SyncTracker = rememberSyncTracker()) =
    remember(syncTracker) { SyncTaskRegistry(syncTracker) }

@Composable
fun SyncTaskRegistry.rememberSyncTask(task: SyncTracker.(force: Boolean) -> Unit): String? {
    var currId: String? by remember { mutableStateOf(null) }
    DisposableEffect(this, task) {
        val id = registerSyncTask(task)
        currId = id
        onDispose {
            unregisterSyncTask(id)
            Snapshot.withMutableSnapshot {
                if (id == currId) currId = null
            }
        }
    }
    return currId
}
