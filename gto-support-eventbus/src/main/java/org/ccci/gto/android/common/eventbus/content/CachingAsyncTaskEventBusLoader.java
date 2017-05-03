package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.support.v4.content.CachingAsyncTaskLoader;
import org.greenrobot.eventbus.EventBus;

public abstract class CachingAsyncTaskEventBusLoader<D> extends CachingAsyncTaskLoader<D>
        implements EventBusLoaderHelper.Interface {
    @NonNull
    private final EventBusLoaderHelper mHelper;

    public CachingAsyncTaskEventBusLoader(@NonNull final Context context) {
        this(context, null);
    }

    public CachingAsyncTaskEventBusLoader(@NonNull final Context context, @Nullable final EventBus eventBus) {
        super(context);
        mHelper = new EventBusLoaderHelper(this, eventBus);
    }

    /* BEGIN lifecycle */

    @Override
    @MainThread
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
    public void addEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mHelper.addEventBusSubscriber(subscriber);
    }

    @Override
    public void removeEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mHelper.removeEventBusSubscriber(subscriber);
    }
}
