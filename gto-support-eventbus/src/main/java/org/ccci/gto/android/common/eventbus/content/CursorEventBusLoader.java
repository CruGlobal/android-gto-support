package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.support.v4.content.SimpleCursorLoader;
import org.greenrobot.eventbus.EventBus;

public abstract class CursorEventBusLoader extends SimpleCursorLoader implements EventBusLoaderHelper.Interface {
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
}
