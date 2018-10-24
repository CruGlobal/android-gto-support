package org.ccci.gto.android.common.db.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Table;
import org.ccci.gto.android.common.support.v4.content.BroadcastReceiverLoaderHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.ccci.gto.android.common.db.AbstractDao.ARG_WHERE;

public class DaoCursorBroadcastReceiverLoader<T> extends DaoCursorLoader<T>
        implements BroadcastReceiverLoaderHelper.Interface {
    @NonNull
    private final BroadcastReceiverLoaderHelper mHelper;

    public DaoCursorBroadcastReceiverLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                            @NonNull final Class<T> type, @Nullable final Bundle args) {
        this(context, dao, Table.forClass(type), args);
    }

    @SuppressWarnings("unchecked")
    public DaoCursorBroadcastReceiverLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                            @NonNull final Table<T> from, @Nullable final Bundle args) {
        super(context, dao, from, args);
        mHelper = new BroadcastReceiverLoaderHelper(this);

        // Handle deprecated WHERE as String
        if (args != null) {
            final String where = args.getString(ARG_WHERE, null);
            if (where != null) {
                throw new IllegalArgumentException(
                        "ARG_WHERE no longer supports a string where clause, pass an Expression instead.");
            }
        }
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

    @Override
    protected void onReset() {
        super.onReset();
        mHelper.onReset();
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
}
