package org.ccci.gto.android.sync.widget

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.ccci.gto.android.common.sync.SyncTask
import org.ccci.gto.android.common.sync.swiperefreshlayout.widget.SwipeRefreshSyncHelper

@Deprecated("Since v3.5.0")
class SwipeRefreshSyncHelper {
    private val mHelper = SwipeRefreshSyncHelper()

    @UiThread
    fun setRefreshLayout(layout: SwipeRefreshLayout?) {
        mHelper.refreshLayout = layout
    }

    fun onRestoreInstanceState(state: Bundle?) = mHelper.onRestoreInstanceState(state)
    fun onSaveInstanceState() = mHelper.onSaveInstanceState()
    fun sync(task: SyncTask) = mHelper.sync(task)
    @UiThread
    fun updateState() = mHelper.updateState()
}
