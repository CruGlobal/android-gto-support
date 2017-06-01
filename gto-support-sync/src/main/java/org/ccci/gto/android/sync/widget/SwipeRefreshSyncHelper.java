package org.ccci.gto.android.sync.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;

import org.ccci.gto.android.common.util.LongSparseBooleanArray;
import org.ccci.gto.android.sync.ThreadedSyncIntentService;
import org.ccci.gto.android.sync.ThreadedSyncIntentService.SyncTask;

public final class SwipeRefreshSyncHelper {
    private static final String EXTRA_ACTIVE_SYNCS = SwipeRefreshSyncHelper.class.getName() + ".ACTIVE_SYNCS";

    @Nullable
    private SwipeRefreshLayout mRefreshLayout;

    @NonNull
    private LongSparseBooleanArray mActiveSyncIds = new LongSparseBooleanArray();

    /* BEGIN lifecycle */

    protected final void onRestoreInstanceState(@Nullable final Bundle state) {
        if (state != null) {
            final LongSparseBooleanArray activeSyncs = state.getParcelable(EXTRA_ACTIVE_SYNCS);
            if (activeSyncs != null) {
                mActiveSyncIds = activeSyncs;
            }
        }
    }

    public final Bundle onSaveInstanceState() {
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(EXTRA_ACTIVE_SYNCS, mActiveSyncIds);
        return bundle;
    }

    /* END lifecycle */

    @UiThread
    public void setRefreshLayout(@Nullable final SwipeRefreshLayout layout) {
        // clear refreshing state on previously set SwipeRefreshLayout
        if (mRefreshLayout != null && mRefreshLayout != layout) {
            mRefreshLayout.setRefreshing(false);
        }

        // update refresh layout & state
        mRefreshLayout = layout;
        updateState();
    }

    public void sync(@NonNull final SyncTask task) {
        mActiveSyncIds.put(task.sync(), true);
        updateState();
    }

    @UiThread
    public void updateState() {
        if (mRefreshLayout != null) {
            boolean refreshing = false;
            for (int i = mActiveSyncIds.size() - 1; i >= 0; i--) {
                if (mActiveSyncIds.valueAt(i) &&
                        ThreadedSyncIntentService.isSyncRunning((int) mActiveSyncIds.keyAt(i))) {
                    refreshing = true;
                    break;
                } else {
                    mActiveSyncIds.removeAt(i);
                }
            }

            mRefreshLayout.setRefreshing(refreshing);
        }
    }
}
