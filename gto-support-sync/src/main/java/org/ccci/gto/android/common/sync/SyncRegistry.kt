package org.ccci.gto.android.common.sync

import android.util.SparseBooleanArray
import java.util.concurrent.atomic.AtomicInteger

private const val INITIAL_SYNC_ID = 1

@Deprecated("Since v4.5.1, we no longer use this module in any of our apps")
object SyncRegistry {
    private val syncsRunning = SparseBooleanArray()
    private val nextSyncId = AtomicInteger(INITIAL_SYNC_ID)

    fun startSync() = nextSyncId.getAndIncrement().also { synchronized(syncsRunning) { syncsRunning.put(it, true) } }
    fun isSyncRunning(id: Int) = id >= INITIAL_SYNC_ID && synchronized(syncsRunning) { syncsRunning.get(id, false) }
    fun finishSync(id: Int) = synchronized(syncsRunning) { syncsRunning.delete(id) }
}
