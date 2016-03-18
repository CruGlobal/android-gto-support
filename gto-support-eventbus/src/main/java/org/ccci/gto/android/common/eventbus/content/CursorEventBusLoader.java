package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

import org.greenrobot.eventbus.EventBus;

public abstract class CursorEventBusLoader extends CursorLoader
        implements EventBusLoaderHelper.Interface {
    @NonNull
    private final EventBusLoaderHelper mHelper;

    public CursorEventBusLoader(@NonNull final Context context) {
        this(context, null, null);
    }

    public CursorEventBusLoader(@NonNull final Context context, @Nullable EventBusSubscriber subscriber) {
        this(context, subscriber, null);
    }

    public CursorEventBusLoader(@NonNull final Context context, @Nullable EventBusSubscriber subscriber,
                                @Nullable EventBus eventBus) {
        super(context);
        mHelper = new EventBusLoaderHelper(this, eventBus);
        if (subscriber != null) {
            mHelper.addEventBusSubscriber(subscriber);
        }
    }

    /* BEGIN lifecycle */

    @Override
    @MainThread
    protected void onStartLoading() {
        super.onStartLoading();
        mHelper.onStartLoading();
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
    public void addEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mHelper.addEventBusSubscriber(subscriber);
    }

    @Override
    public void removeEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mHelper.removeEventBusSubscriber(subscriber);
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
