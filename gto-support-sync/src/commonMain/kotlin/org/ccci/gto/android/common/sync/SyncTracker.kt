package org.ccci.gto.android.common.sync

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncTracker(private val scope: CoroutineScope) {
    companion object {
        private const val TAG = "SyncTracker"
    }

    private val _isInitialSyncFinished = MutableStateFlow(false)
    val isInitialSyncFinished = _isInitialSyncFinished.asStateFlow()
    private val syncsRunning = MutableStateFlow(0)
    val isSyncing = syncsRunning.map { it > 0 }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), false)

    fun launchSync(block: suspend () -> Unit) {
        scope.launch { runSync(block) }
    }

    suspend fun runSync(block: suspend () -> Unit) {
        syncsRunning.update { it + 1 }
        try {
            block()
            _isInitialSyncFinished.value = true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Logger.withTag(TAG).e("Unhandled error running sync task", e)
        } finally {
            syncsRunning.update { it - 1 }
        }
    }
}
