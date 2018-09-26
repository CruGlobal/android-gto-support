package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

public class LoaderBroadcastReceiver extends BroadcastReceiver {
    @NonNull
    private final Loader mLoader;

    public LoaderBroadcastReceiver(@NonNull final Loader loader) {
        mLoader = loader;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mLoader.onContentChanged();
    }
}
