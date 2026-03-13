package org.ccci.gto.android.common.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun rememberSyncTracker(initialSync: (SyncTracker) -> Unit = {}): SyncTracker {
    val scope = rememberCoroutineScope()
    return remember { SyncTracker(scope).also { initialSync(it) } }
}
