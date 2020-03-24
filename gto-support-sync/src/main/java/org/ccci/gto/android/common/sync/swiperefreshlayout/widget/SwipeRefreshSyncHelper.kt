package org.ccci.gto.android.common.sync.swiperefreshlayout.widget

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.ccci.gto.android.common.androidx.collection.LongSparseBooleanArray
import org.ccci.gto.android.common.androidx.collection.mutableKeyIterator
import org.ccci.gto.android.common.sync.SyncRegistry.isSyncRunning
import org.ccci.gto.android.common.sync.SyncTask

private const val EXTRA_ACTIVE_SYNCS = "org.ccci.gto.android.common.sync.SwipeRefreshSyncHelper.ACTIVE_SYNCS"

class SwipeRefreshSyncHelper {
    private val activeSyncIds = LongSparseBooleanArray()

    @set:UiThread
    var refreshLayout: SwipeRefreshLayout? = null
        set(value) {
            if (field == value) return

            field?.isRefreshing = false
            field = value

            updateState()
        }

    // region Lifecycle
    fun onRestoreInstanceState(state: Bundle?) {
        state?.getParcelable<LongSparseBooleanArray>(EXTRA_ACTIVE_SYNCS)
            ?.let { activeSyncIds.putAll(it) }
    }

    fun onSaveInstanceState() = Bundle(1).apply {
        putParcelable(EXTRA_ACTIVE_SYNCS, activeSyncIds)
    }
    // endregion Lifecycle

    fun sync(task: SyncTask) {
        activeSyncIds.put(task.sync().toLong(), true)
        updateState()
    }

    @UiThread
    fun updateState() {
        refreshLayout?.isRefreshing = activeSyncIds.mutableKeyIterator().run {
            while (hasNext()) {
                if (!isSyncRunning(next().toInt())) {
                    remove()
                    continue
                }
                return@run true
            }
            false
        }
    }
}
