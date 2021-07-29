package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.support.v4.content.SimpleCursorLoader;
import org.greenrobot.eventbus.EventBus;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
public abstract class CursorEventBusLoader extends SimpleCursorLoader implements EventBusLoaderHelper.Interface {
    @NonNull
    private final EventBusLoaderHelper mHelper;

    public CursorEventBusLoader(@NonNull final Context context) {
        this(context, (EventBus) null);
    }

    public CursorEventBusLoader(@NonNull final Context context, @Nullable final EventBus eventBus) {
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
