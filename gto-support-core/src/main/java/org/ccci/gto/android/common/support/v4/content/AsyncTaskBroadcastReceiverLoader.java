package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AsyncTaskBroadcastReceiverLoader<D> extends CachingAsyncTaskLoader<D>
        implements BroadcastReceiverLoaderHelper.Interface {
    private final BroadcastReceiverLoaderHelper mHelper;

    private D mData;

    public AsyncTaskBroadcastReceiverLoader(@NonNull final Context context) {
        super(context);
        mHelper = new BroadcastReceiverLoaderHelper(this);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        mHelper.onStartLoading();
        super.onStartLoading();
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        mHelper.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();
        mHelper.onReset();
    }

    /* END lifecycle */

    @Override
    public final void addIntentFilter(@NonNull final IntentFilter filter) {
        mHelper.addIntentFilter(filter);
    }

    @Override
    public final void setBroadcastReceiver(@Nullable final BroadcastReceiver receiver) {
        mHelper.setBroadcastReceiver(receiver);
    }
}
