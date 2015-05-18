package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

public class Query<T> {
    @NonNull
    final Class<T> mType;
    final boolean mDistinct;
    @NonNull
    final Join<T, ?>[] mJoins;
    @Nullable
    final String[] mProjection;
    @Nullable
    final String mOrderBy;
    @Nullable
    final String mWhere;
    @NonNull
    final String[] mWhereArgs;

    @SuppressWarnings("unchecked")
    private Query(@NonNull final Class<T> type, final boolean distinct, @Nullable final Join<T, ?>[] joins,
                  @Nullable final String[] projection, @Nullable final String where, @Nullable final String[] whereArgs,
                  @Nullable final String orderBy) {
        mType = type;
        mDistinct = distinct;
        mJoins = joins != null ? joins : Join.NO_JOINS;
        mProjection = projection != null && projection.length > 0 ? projection : null;
        mOrderBy = orderBy;
        mWhere = where;
        mWhereArgs = whereArgs != null ? whereArgs : new String[0];
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Class<T> type) {
        return new Query<>(type, false, null, null, null, null, null);
    }

    @NonNull
    public Query<T> distinct(final boolean distinct) {
        return new Query<>(mType, distinct, mJoins, mProjection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Query<T> join(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mType, mDistinct, ArrayUtils.merge(Join.class, mJoins, joins), mProjection, mWhere,
                           mWhereArgs, mOrderBy);
    }

    @NonNull
    public Query<T> joins(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mType, mDistinct, joins, mProjection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    public Query<T> projection(@Nullable final String... projection) {
        return new Query<>(mType, mDistinct, mJoins, projection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    Query<T> where(@Nullable final Pair<String, String[]> where) {
        if (where != null) {
            return new Query<>(mType, mDistinct, mJoins, mProjection, where.first, where.second, mOrderBy);
        } else {
            return new Query<>(mType, mDistinct, mJoins, mProjection, null, null, mOrderBy);
        }
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final Object... args) {
        return where(where, args != null ? bindValues(args) : null);
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final String... args) {
        return new Query<>(mType, mDistinct, mJoins, mProjection, where, args, mOrderBy);
    }

    @NonNull
    public Query<T> orderBy(@Nullable final String orderBy) {
        return new Query<>(mType, mDistinct, mJoins, mProjection, mWhere, mWhereArgs, orderBy);
    }
}
