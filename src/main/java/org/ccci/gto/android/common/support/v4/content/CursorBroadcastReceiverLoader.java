package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

public abstract class CursorBroadcastReceiverLoader extends CursorLoader
        implements BroadcastReceiverLoaderHelper.Interface {
    private final BroadcastReceiverLoaderHelper mHelper;

    public CursorBroadcastReceiverLoader(final Context context, final IntentFilter... filters) {
        super(context);
        mHelper = new BroadcastReceiverLoaderHelper(this, filters);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mHelper.onStartLoading();
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

    @Override
    public final Cursor loadInBackground() {
        final Cursor c = this.getCursor();
        if(c != null) {
            c.getCount();
        }
        return c;
    }

    protected abstract Cursor getCursor();
}
