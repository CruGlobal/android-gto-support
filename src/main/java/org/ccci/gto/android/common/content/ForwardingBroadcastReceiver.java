package org.ccci.gto.android.common.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

public abstract class ForwardingBroadcastReceiver extends BroadcastReceiver {
    private BroadcastReceiver mDelegate;

    protected ForwardingBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (mDelegate != null) {
            mDelegate.onReceive(context, intent);
        }
    }

    public final void setDelegate(@Nullable final BroadcastReceiver receiver) {
        mDelegate = receiver;
    }
}
