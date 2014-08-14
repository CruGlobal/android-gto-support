package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AsyncTaskBroadcastReceiverLoader<D> extends AsyncTaskLoader<D>
        implements BroadcastReceiverLoaderHelper.Interface {
    private final BroadcastReceiverLoaderHelper mHelper;

    private D mData;

    public AsyncTaskBroadcastReceiverLoader(final Context context, final IntentFilter... filters) {
        super(context);
        mHelper = new BroadcastReceiverLoaderHelper(this, filters);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mHelper.onStartLoading();

        // deliver already loaded data
        if (mData != null) {
            deliverResult(mData);
        }

        // force a fresh load if needed
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        mHelper.onAbandon();
    }

    /* END lifecycle */

    @Override
    public final void addIntentFilter(final IntentFilter filter) {
        mHelper.addIntentFilter(filter);
    }

    @Override
    public final void setBroadcastReceiver(final LoaderBroadcastReceiver receiver) {
        mHelper.setBroadcastReceiver(receiver);
    }
}
