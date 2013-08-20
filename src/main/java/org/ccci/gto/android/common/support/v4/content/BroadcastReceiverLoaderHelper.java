package org.ccci.gto.android.common.support.v4.content;

import android.content.IntentFilter;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public final class BroadcastReceiverLoaderHelper {
    public static interface Interface {
        void addIntentFilter(IntentFilter filter);
    }

    private final LocalBroadcastManager localBroadcastManager;
    private final LoaderBroadcastReceiver receiver;
    private final List<IntentFilter> filters = new ArrayList<IntentFilter>();

    public BroadcastReceiverLoaderHelper(final Loader loader, final IntentFilter... filters) {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(loader.getContext());
        this.receiver = new LoaderBroadcastReceiver(loader);
        for(final IntentFilter filter : filters) {
            this.addIntentFilter(filter);
        }
    }

    public void addIntentFilter(final IntentFilter filter) {
        this.filters.add(filter);
    }

    void onStartLoading() {
        if (this.localBroadcastManager != null && this.filters.size() > 0) {
            for (final IntentFilter filter : this.filters) {
                localBroadcastManager.registerReceiver(this.receiver, filter);
            }
        }
    }

    void onAbandon() {
        if (this.localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(this.receiver);
        }
    }
}
