package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;

public class LoaderBroadcastReceiver extends BroadcastReceiver {
    private final Loader mLoader;

    public LoaderBroadcastReceiver(final Loader loader) {
        mLoader = loader;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mLoader.onContentChanged();
    }
}
