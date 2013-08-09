package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;

public class LoaderBroadcastReceiver extends BroadcastReceiver {
    private final Loader loader;

    public LoaderBroadcastReceiver(final Loader loader) {
        this.loader = loader;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        loader.onContentChanged();
    }
}
