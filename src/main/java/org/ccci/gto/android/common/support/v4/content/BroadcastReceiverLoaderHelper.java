package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

final class BroadcastReceiverLoaderHelper {
    public static interface Interface {
        void addIntentFilter(IntentFilter filter);

        void setBroadcastReceiver(LoaderBroadcastReceiver receiver);
    }

    private final LocalBroadcastManager mLocalBroadcastManager;
    private final Loader mLoader;
    private LoaderBroadcastReceiver mReceiver;
    private final ArrayList<IntentFilter> mFilters = new ArrayList<>();

    BroadcastReceiverLoaderHelper(final Loader loader, final IntentFilter... filters) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(loader.getContext());
        mLoader = loader;
        this.setBroadcastReceiver(null);
        for (final IntentFilter filter : filters) {
            this.addIntentFilter(filter);
        }
    }

    void addIntentFilter(final IntentFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter cannot be null");
        }

        mFilters.add(filter);

        // register filter if Loader is already started
        if (mLoader.isStarted()) {
            synchronized (this) {
                mLocalBroadcastManager.registerReceiver(mReceiver, filter);
            }
        }
    }

    void setBroadcastReceiver(LoaderBroadcastReceiver receiver) {
        if (receiver == null) {
            receiver = new LoaderBroadcastReceiver(mLoader);
        }

        synchronized (this) {
            // register the new receiver and unregister the old receiver. we overlap registrations to not drop broadcasts
            if (mLoader.isStarted()) {
                registerReceiver(receiver);
                mLocalBroadcastManager.unregisterReceiver(mReceiver);
            }

            // save new receiver
            mReceiver = receiver;
        }
    }

    void onStartLoading() {
        registerReceiver(mReceiver);
    }

    void onAbandon() {
        synchronized (this) {
            mLocalBroadcastManager.unregisterReceiver(mReceiver);
        }
    }

    private synchronized void registerReceiver(final BroadcastReceiver receiver) {
        for (final IntentFilter filter : mFilters) {
            mLocalBroadcastManager.registerReceiver(receiver, filter);
        }
    }
}
