package org.ccci.gto.android.common.eventbus.content;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Table;
import org.ccci.gto.android.common.db.support.v4.content.DaoCursorLoader;
import org.greenrobot.eventbus.EventBus;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
public class DaoCursorEventBusLoader<T> extends DaoCursorLoader<T> implements EventBusLoaderHelper.Interface {
    private final EventBusLoaderHelper mHelper;

    public DaoCursorEventBusLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                   @NonNull final Class<T> type, @Nullable final Bundle args) {
        this(context, null, dao, Table.forClass(type), args);
    }

    public DaoCursorEventBusLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                   @NonNull final Table<T> from, @Nullable final Bundle args) {
        this(context, null, dao, from, args);
    }

    public DaoCursorEventBusLoader(@NonNull final Context context, @Nullable final EventBus eventBus,
                                   @NonNull final AbstractDao dao, @NonNull final Table<T> from,
                                   @Nullable final Bundle args) {
        super(context, dao, from, args);
        mHelper = new EventBusLoaderHelper(this, eventBus);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        mHelper.onStartLoading();
        super.onStartLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
        mHelper.onReset();
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        mHelper.onAbandon();
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
