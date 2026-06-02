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
fun SyncTaskRegistry.rememberSyncTask(task: SyncTracker.(force: Boolean) -> Unit) =
    rememberSyncTask(keys = emptyArray(), task)

@Composable
fun SyncTaskRegistry.rememberSyncTask(key1: Any?, task: SyncTracker.(force: Boolean) -> Unit) =
    rememberSyncTask(keys = arrayOf(key1), task)

@Composable
fun SyncTaskRegistry.rememberSyncTask(key1: Any?, key2: Any?, task: SyncTracker.(force: Boolean) -> Unit) =
    rememberSyncTask(keys = arrayOf(key1, key2), task)

@Composable
fun SyncTaskRegistry.rememberSyncTask(vararg keys: Any?, task: SyncTracker.(force: Boolean) -> Unit): String? {
    var currId: String? by remember { mutableStateOf(null) }
    DisposableEffect(this, *keys, task) {
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
