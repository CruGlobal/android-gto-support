package org.ccci.gto.android.common.db.support.v4.content;

import static org.ccci.gto.android.common.db.AbstractDao.ARG_DISTINCT;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_JOINS;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_ORDER_BY;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_PROJECTION;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_WHERE;
import static org.ccci.gto.android.common.db.AbstractDao.ARG_WHERE_ARGS;
import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Join;
import org.ccci.gto.android.common.db.Query;
import org.ccci.gto.android.common.db.Table;
import org.ccci.gto.android.common.support.v4.content.CursorBroadcastReceiverLoader;
import org.ccci.gto.android.common.util.BundleUtils;

public class DaoCursorBroadcastReceiverLoader<T> extends CursorBroadcastReceiverLoader {
    private static final Pair<String, String[]> NO_WHERE = Pair.create(null, new String[0]);

    @NonNull
    private final AbstractDao mDao;

    private boolean mDistinct = false;
    @NonNull
    private final Table<T> mFrom;
    @NonNull
    @SuppressWarnings("unchecked")
    private Join<T, ?>[] mJoins = Join.NO_JOINS;
    @NonNull
    private Pair<String, String[]> mWhere = NO_WHERE;

    public DaoCursorBroadcastReceiverLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                            @NonNull final Class<T> type, @Nullable final Bundle args) {
        this(context, dao, Table.forClass(type), args);
    }

    @SuppressWarnings("unchecked")
    public DaoCursorBroadcastReceiverLoader(@NonNull final Context context, @NonNull final AbstractDao dao,
                                            @NonNull final Table<T> from, @Nullable final Bundle args) {
        super(context);
        mDao = dao;

        mFrom = from;
        if (args != null) {
            setDistinct(args.getBoolean(ARG_DISTINCT, false));
            setJoins(BundleUtils.getParcelableArray(args, ARG_JOINS, Join.class));
            setProjection(args.getStringArray(ARG_PROJECTION));
            setWhere(args.getString(ARG_WHERE), args.getStringArray(ARG_WHERE_ARGS));
            setSortOrder(args.getString(ARG_ORDER_BY));
        } else {
            setDistinct(false);
            setJoins(null);
            setProjection(null);
            setWhere(null);
            setSortOrder(null);
        }
    }

    @Nullable
    @Override
    protected final Cursor getCursor() {
        // build query
        final Pair<String, String[]> where = getWhere();
        return mDao.getCursor(
                Query.select(mFrom).distinct(isDistinct()).joins(getJoins()).projection(getProjection()).where(
                        where.first, where.second).orderBy(getSortOrder()));
    }

    public void setDistinct(final boolean distinct) {
        mDistinct = distinct;
    }

    public boolean isDistinct() {
        return mDistinct;
    }

    @SuppressWarnings("unchecked")
    public void setJoins(@Nullable final Join<T, ?>[] joins) {
        mJoins = joins != null ? joins : Join.NO_JOINS;
    }

    @NonNull
    public Join<T, ?>[] getJoins() {
        return mJoins;
    }

    @Override
    public void setProjection(@Nullable final String[] projection) {
        super.setProjection(projection != null ? projection : mDao.getFullProjection(mFrom));
    }

    @NonNull
    public String[] getProjection() {
        final String[] projection = super.getProjection();
        return projection != null ? projection : mDao.getFullProjection(mFrom);
    }

    public void setWhere(@Nullable final String where, @Nullable final Object... args) {
        setWhere(where, args != null ? bindValues(args) : null);
    }

    public void setWhere(@Nullable final String where, @Nullable final String... args) {
        mWhere = Pair.create(where, args);
    }

    @NonNull
    public Pair<String, String[]> getWhere() {
        return mWhere;
    }
}
