package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
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
