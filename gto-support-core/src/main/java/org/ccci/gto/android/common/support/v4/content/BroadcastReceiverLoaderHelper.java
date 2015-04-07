package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

final class BroadcastReceiverLoaderHelper {
    public interface Interface {
        void addIntentFilter(@NonNull IntentFilter filter);

        void setBroadcastReceiver(@Nullable BroadcastReceiver receiver);
    }

    @NonNull
    private final LocalBroadcastManager mLocalBroadcastManager;
    @NonNull
    private final Loader mLoader;
    @NonNull
    private BroadcastReceiver mReceiver;
    private final ArrayList<IntentFilter> mFilters = new ArrayList<>();

    BroadcastReceiverLoaderHelper(@NonNull final Loader loader) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(loader.getContext());
        mLoader = loader;
        this.setBroadcastReceiver(null);
    }

    void addIntentFilter(@NonNull final IntentFilter filter) {
        mFilters.add(filter);

        // register filter if Loader is already started
        if (mLoader.isStarted()) {
            synchronized (this) {
                mLocalBroadcastManager.registerReceiver(mReceiver, filter);
            }
        }
    }

    void setBroadcastReceiver(@Nullable BroadcastReceiver receiver) {
        if (receiver == null) {
            receiver = new LoaderBroadcastReceiver(mLoader);
        }

        synchronized (this) {
            // register the new receiver and unregister the old receiver. we overlap registrations to not drop broadcasts
            if (mLoader.isStarted()) {
                registerReceiver(receiver);
                unregisterReceiver(mReceiver);
            }

            // save new receiver
            mReceiver = receiver;
        }
    }

    void onStartLoading() {
        synchronized (this) {
            registerReceiver(mReceiver);
        }
    }

    void onAbandon() {
        synchronized (this) {
            unregisterReceiver(mReceiver);
        }
    }

    private synchronized void registerReceiver(@NonNull final BroadcastReceiver receiver) {
        for (final IntentFilter filter : mFilters) {
            mLocalBroadcastManager.registerReceiver(receiver, filter);
        }
    }

    private synchronized void unregisterReceiver(@NonNull final BroadcastReceiver receiver) {
        mLocalBroadcastManager.unregisterReceiver(receiver);
    }
}
