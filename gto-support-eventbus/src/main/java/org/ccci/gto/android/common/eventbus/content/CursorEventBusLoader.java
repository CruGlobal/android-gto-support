package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

public abstract class CursorEventBusLoader extends CursorLoader
        implements EventBusLoaderHelper.Interface {
    @NonNull
    private final EventBusLoaderHelper mHelper;

    public CursorEventBusLoader(@NonNull final Context context) {
        this(context, this);
    }

    public CursorEventBusLoader(@NonNull final Context context, @NonNull final Object listener) {
        super(context);
        mHelper = new EventBusLoaderHelper(this, listener);
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
    public void setEventBusListener(@NonNull Object listener) {
        mHelper.setEventBusListener(listener);
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
