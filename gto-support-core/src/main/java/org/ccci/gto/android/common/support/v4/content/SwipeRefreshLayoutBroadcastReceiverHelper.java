package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
public class SwipeRefreshLayoutBroadcastReceiverHelper {
    SwipeRefreshLayout mRefreshLayout = null;

    private LocalBroadcastManager mLocalBroadcastManager = null;
    private boolean mReceiving = false;
    private final List<IntentFilter> mStartFilters = new ArrayList<>();
    private final List<IntentFilter> mFinishFilters = new ArrayList<>();

    private final BroadcastReceiver mStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (mRefreshLayout != null) {
                mRefreshLayout.setRefreshing(true);
            }
        }
    };
    private final BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (mRefreshLayout != null) {
                mRefreshLayout.setRefreshing(false);
            }
        }
    };

    public void setRefreshLayout(final SwipeRefreshLayout layout) {
        if (mReceiving) {
            throw new IllegalStateException("can't set a refresh layout while receiving broadcasts");
        }

        mRefreshLayout = layout;
    }

    public void addStartFilters(final IntentFilter... filters) {
        mStartFilters.addAll(Arrays.asList(filters));

        // register new receivers if we are already receiving broadcasts
        if (mReceiving) {
            if (mLocalBroadcastManager != null) {
                for (final IntentFilter filter : filters) {
                    mLocalBroadcastManager.registerReceiver(mStartReceiver, filter);
                }
            }
        }
    }

    public void addFinishFilters(final IntentFilter... filters) {
        mFinishFilters.addAll(Arrays.asList(filters));

        // register new receivers if we are already receiving broadcasts
        if (mReceiving) {
            if (mLocalBroadcastManager != null) {
                for (final IntentFilter filter : filters) {
                    mLocalBroadcastManager.registerReceiver(mFinishReceiver, filter);
                }
            }
        }
    }

    public void startReceiving(final LocalBroadcastManager bm) {
        if (!mReceiving) {
            if (mRefreshLayout == null) {
                throw new IllegalStateException("can't start receiving without a refresh layout");
            }

            // record that we are receiving broadcasts
            mReceiving = true;

            // start listening for broadcasts
            mLocalBroadcastManager = bm;
            for (final IntentFilter filter : mStartFilters) {
                mLocalBroadcastManager.registerReceiver(mStartReceiver, filter);
            }
            for (final IntentFilter filter : mFinishFilters) {
                mLocalBroadcastManager.registerReceiver(mFinishReceiver, filter);
            }
        }
    }

    public void stopReceiving() {
        if (mReceiving) {
            // unregister receivers from LocalBroadcastManager
            if (mLocalBroadcastManager != null) {
                mLocalBroadcastManager.unregisterReceiver(mStartReceiver);
                mLocalBroadcastManager.unregisterReceiver(mFinishReceiver);
                mLocalBroadcastManager = null;
            }

            mReceiving = false;
        }
    }
}
