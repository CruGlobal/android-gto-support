package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastReceiverCursorLoader extends CursorLoader {
    private final LocalBroadcastManager localBroadcastManager;
    private final LoaderBroadcastReceiver receiver = new LoaderBroadcastReceiver(this);
    private final List<IntentFilter> filters = new ArrayList<IntentFilter>();

    public BroadcastReceiverCursorLoader(final Context context, final IntentFilter... filters) {
        super(context);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        for(final IntentFilter filter : filters) {
            this.addIntentFilter(filter);
        }
    }

    public final void addIntentFilter(final IntentFilter filter) {
        this.filters.add(filter);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (this.localBroadcastManager != null && this.filters.size() > 0) {
            for (final IntentFilter filter : this.filters) {
                localBroadcastManager.registerReceiver(this.receiver, filter);
            }
        }
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        if (this.localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(this.receiver);
        }
    }

    /* END lifecycle */

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
