package org.ccci.gto.android.common.sync

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.slack.circuit.runtime.CircuitContext

@VisibleForTesting
internal var CircuitContext.syncTaskRegistry: SyncTaskRegistry?
    get() = tag() ?: parent?.syncTaskRegistry
    set(value) = putTag(value)

@Composable
fun CircuitContext.rememberSyncTaskRegistry(syncTracker: SyncTracker = rememberSyncTracker()): SyncTaskRegistry {
    val registry = remember(this, syncTracker) { SyncTaskRegistry(syncTracker) }
    DisposableEffect(this, registry) {
        syncTaskRegistry = registry
        onDispose { syncTaskRegistry = null }
    }
    return registry
}

@Composable
fun CircuitContext.rememberSyncTask(task: SyncTracker.(force: Boolean) -> Unit) =
    syncTaskRegistry?.rememberSyncTask(task)
