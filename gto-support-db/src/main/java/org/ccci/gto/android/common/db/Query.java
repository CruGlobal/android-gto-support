package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

public final class Query<T> {
    @NonNull
    final Table<T> mTable;
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
    private Query(@NonNull final Table<T> table, final boolean distinct, @Nullable final Join<T, ?>[] joins,
                  @Nullable final String[] projection, @Nullable final String where, @Nullable final String[] whereArgs,
                  @Nullable final String orderBy) {
        mTable = table;
        mDistinct = distinct;
        mJoins = joins != null ? joins : Join.NO_JOINS;
        mProjection = projection != null && projection.length > 0 ? projection : null;
        mOrderBy = orderBy;
        mWhere = where;
        mWhereArgs = whereArgs != null ? whereArgs : new String[0];
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Class<T> type) {
        return select(Table.forClass(type));
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Table<T> table) {
        return new Query<>(table, false, null, null, null, null, null);
    }

    @NonNull
    public Query<T> distinct(final boolean distinct) {
        return new Query<>(mTable, distinct, mJoins, mProjection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Query<T> join(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, ArrayUtils.merge(Join.class, mJoins, joins), mProjection, mWhere,
                           mWhereArgs, mOrderBy);
    }

    @NonNull
    public Query<T> joins(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, joins, mProjection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    public Query<T> projection(@Nullable final String... projection) {
        return new Query<>(mTable, mDistinct, mJoins, projection, mWhere, mWhereArgs, mOrderBy);
    }

    @NonNull
    Query<T> where(@Nullable final Pair<String, String[]> where) {
        if (where != null) {
            return new Query<>(mTable, mDistinct, mJoins, mProjection, where.first, where.second, mOrderBy);
        } else {
            return new Query<>(mTable, mDistinct, mJoins, mProjection, null, null, mOrderBy);
        }
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final Object... args) {
        return where(where, args != null ? bindValues(args) : null);
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final String... args) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, where, args, mOrderBy);
    }

    @NonNull
    public Query<T> orderBy(@Nullable final String orderBy) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mWhereArgs, orderBy);
    }

    final Pair<String, String[]> buildSqlFrom(@NonNull final AbstractDao dao) {
        // joins need to be passed appended to the table name
        final StringBuilder sb = new StringBuilder(mTable.sqlTable(dao));
        String[] args = null;
        for (final Join<T, ?> joinObj : mJoins) {
            final Pair<String, String[]> join = joinObj.buildSql(dao);
            sb.append(join.first);
            args = ArrayUtils.merge(String.class, args, join.second);
        }
        return Pair.create(sb.toString(), args);

    }
}
