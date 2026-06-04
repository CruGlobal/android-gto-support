package org.ccci.gto.android.common.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.slack.circuit.runtime.CircuitContext

var CircuitContext.syncTaskRegistry: SyncTaskRegistry?
    get() = tag() ?: parent?.syncTaskRegistry
    set(value) = putTag(value)

@Deprecated("CircuitContext is meant to be used in a Presenter/Ui Factory and not in a Composable")
@Composable
fun CircuitContext.rememberSyncTaskRegistry(syncTracker: SyncTracker = rememberSyncTracker()): SyncTaskRegistry {
    val registry = remember(this, syncTracker) { SyncTaskRegistry(syncTracker) }
    DisposableEffect(this, registry) {
        syncTaskRegistry = registry
        onDispose { syncTaskRegistry = null }
    }
    return registry
}

@Deprecated("CircuitContext is meant to be used in a Presenter/Ui Factory and not in a Composable")
@Composable
fun CircuitContext.rememberSyncTask(task: SyncTracker.(force: Boolean) -> Unit) =
    syncTaskRegistry?.rememberSyncTask(task)

@Deprecated("CircuitContext is meant to be used in a Presenter/Ui Factory and not in a Composable")
@Composable
fun CircuitContext.rememberSyncTask(key1: Any?, task: SyncTracker.(force: Boolean) -> Unit) =
    syncTaskRegistry?.rememberSyncTask(key1, task)

@Deprecated("CircuitContext is meant to be used in a Presenter/Ui Factory and not in a Composable")
@Composable
fun CircuitContext.rememberSyncTask(key1: Any?, key2: Any?, task: SyncTracker.(force: Boolean) -> Unit) =
    syncTaskRegistry?.rememberSyncTask(key1, key2, task)

@Deprecated("CircuitContext is meant to be used in a Presenter/Ui Factory and not in a Composable")
@Composable
fun CircuitContext.rememberSyncTask(vararg keys: Any?, task: SyncTracker.(force: Boolean) -> Unit) =
    syncTaskRegistry?.rememberSyncTask(keys = keys, task)
