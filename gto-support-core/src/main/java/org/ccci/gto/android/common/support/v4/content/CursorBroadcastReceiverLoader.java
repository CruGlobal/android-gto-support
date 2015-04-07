package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

public abstract class CursorBroadcastReceiverLoader extends CursorLoader
        implements BroadcastReceiverLoaderHelper.Interface {
    @NonNull
    private final BroadcastReceiverLoaderHelper mHelper;

    public CursorBroadcastReceiverLoader(@NonNull final Context context) {
        super(context);
        mHelper = new BroadcastReceiverLoaderHelper(this);
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
    public final void addIntentFilter(@NonNull final IntentFilter filter) {
        mHelper.addIntentFilter(filter);
    }

    @Override
    public final void setBroadcastReceiver(@Nullable final BroadcastReceiver receiver) {
        mHelper.setBroadcastReceiver(receiver);
    }

    @Nullable
    @Override
    public final Cursor loadInBackground() {
        final Cursor c = this.getCursor();
        if (c != null) {
            c.getCount();
        }
        return c;
    }

    @Nullable
    protected abstract Cursor getCursor();
}
