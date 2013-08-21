package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AsyncTaskBroadcastReceiverLoader<D> extends AsyncTaskLoader<D>
        implements BroadcastReceiverLoaderHelper.Interface {
    private final BroadcastReceiverLoaderHelper helper;

    private D data;

    public AsyncTaskBroadcastReceiverLoader(final Context context, final IntentFilter... filters) {
        super(context);
        this.helper = new BroadcastReceiverLoaderHelper(this, filters);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        this.helper.onStartLoading();

        // deliver already loaded data
        if (data != null) {
            deliverResult(data);
        }

        // force a fresh load if needed
        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        this.helper.onAbandon();
    }

    /* END lifecycle */

    @Override
    public final void addIntentFilter(final IntentFilter filter) {
        this.helper.addIntentFilter(filter);
    }
}
