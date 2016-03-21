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
    final Expression mWhere;
    @Nullable
    final String mGroupBy;
    @Nullable
    final Expression mHaving;

    @SuppressWarnings("unchecked")
    private Query(@NonNull final Table<T> table, final boolean distinct, @Nullable final Join<T, ?>[] joins,
                  @Nullable final String[] projection, @Nullable final Expression where,
                  @Nullable final String orderBy, @Nullable final String groupBy, @Nullable final Expression having) {
        mTable = table;
        mDistinct = distinct;
        mJoins = joins != null ? joins : Join.NO_JOINS;
        mProjection = projection != null && projection.length > 0 ? projection : null;
        mOrderBy = orderBy;
        mWhere = where;
        mGroupBy = groupBy;
        mHaving = having;
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Class<T> type) {
        return select(Table.forClass(type));
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Table<T> table) {
        return new Query<>(table, false, null, null, null, null, null, null);
    }

    @NonNull
    public Query<T> distinct(final boolean distinct) {
        return new Query<>(mTable, distinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, mHaving);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Query<T> join(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, ArrayUtils.merge(Join.class, mJoins, joins), mProjection, mWhere,
                           mOrderBy, mGroupBy, mHaving);
    }

    @NonNull
    public Query<T> joins(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, joins, mProjection, mWhere, mOrderBy, mGroupBy,
                           mHaving);
    }

    @NonNull
    public Query<T> projection(@Nullable final String... projection) {
        return new Query<>(mTable, mDistinct, mJoins, projection, mWhere, mOrderBy, mGroupBy,
                           mHaving);
    }

    @NonNull
    public Query<T> where(@Nullable final Expression where) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, where, mOrderBy, mGroupBy,
                           mHaving);
    }

    @NonNull
    @Deprecated
    Query<T> where(@Nullable final Pair<String, String[]> where) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection,
                           where != null ? Expression.raw(where.first, where.second) : null,
                           mOrderBy, mGroupBy, mHaving);
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @NonNull final Object... args) {
        return where(where, bindValues(args));
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final String... args) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, where != null ? Expression.raw(where, args) : null,
                           mOrderBy, mGroupBy, mHaving);
    }

    @NonNull
    public Query<T> orderBy(@Nullable final String orderBy) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, orderBy, mGroupBy, mHaving);
    }

    @NonNull
    public Query<T> groupBy(@Nullable final String groupBy) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, groupBy, mHaving);
    }

    @NonNull
    public Query<T> having(@Nullable final Expression having) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, having);
    }

    @NonNull
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

    @NonNull
    final Pair<String, String[]> buildSqlWhere(@NonNull final AbstractDao dao) {
        return mWhere != null ? mWhere.buildSql(dao) : Pair.<String, String[]>create(null, null);
    }

    @NonNull
    final Pair<String, String[]> buildSqlHaving(@NonNull final AbstractDao dao) {
        return mHaving != null ? mHaving.buildSql(dao) : Pair.<String, String[]>create(null, null);
    }
}
