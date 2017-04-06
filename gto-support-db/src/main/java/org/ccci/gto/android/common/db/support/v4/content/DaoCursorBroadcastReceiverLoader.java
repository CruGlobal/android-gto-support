package org.ccci.gto.android.common.db.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.compat.os.BundleCompat;
import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Expression;
import org.ccci.gto.android.common.db.Table;
import org.ccci.gto.android.common.support.v4.content.BroadcastReceiverLoaderHelper;

import static org.ccci.gto.android.common.db.AbstractDao.ARG_WHERE;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_WHERE_ARGS;

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
            final String where = BundleCompat.getString(args, ARG_WHERE, null);
            if (where != null) {
                setWhere(where, args.getStringArray(ARG_WHERE_ARGS));
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

    /**
     * @deprecated Since v0.9.0, use {@link DaoCursorBroadcastReceiverLoader#setWhere(Expression)} instead.
     */
    @Deprecated
    public void setWhere(@Nullable final String where, @Nullable final String... args) {
        setWhere(where != null ? Expression.raw(where, args) : null);
    }
}
