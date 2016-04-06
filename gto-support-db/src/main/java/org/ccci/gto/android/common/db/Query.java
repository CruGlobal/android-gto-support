package org.ccci.gto.android.common.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.util.ArrayUtils;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

public final class Query<T> {
    private static final Field[] NO_FIELDS = new Field[0];

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
    @NonNull
    final Field[] mGroupBy;
    @Nullable
    final Expression mHaving;
    @Nullable
    final Integer mLimit;
    @Nullable
    final Integer mOffset;

    @SuppressWarnings("unchecked")
    private Query(@NonNull final Table<T> table, final boolean distinct, @Nullable final Join<T, ?>[] joins,
                  @Nullable final String[] projection, @Nullable final Expression where,
                  @Nullable final String orderBy, @Nullable final Field[] groupBy, @Nullable final Expression having,
                  @Nullable final Integer limit, @Nullable final Integer offset) {
        mTable = table;
        mDistinct = distinct;
        mJoins = joins != null ? joins : Join.NO_JOINS;
        mProjection = projection != null && projection.length > 0 ? projection : null;
        mOrderBy = orderBy;
        mWhere = where;
        mGroupBy = groupBy != null ? groupBy : NO_FIELDS;
        mHaving = having;
        mLimit = limit;
        mOffset = offset;
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Class<T> type) {
        return select(Table.forClass(type));
    }

    @NonNull
    public static <T> Query<T> select(@NonNull final Table<T> table) {
        return new Query<>(table, false, null, null, null, null, null, null, null, null);
    }

    @NonNull
    public Query<T> distinct(final boolean distinct) {
        return new Query<>(mTable, distinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final Query<T> join(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, ArrayUtils.merge(Join.class, mJoins, joins), mProjection, mWhere,
                           mOrderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    @SafeVarargs
    public final Query<T> joins(@NonNull final Join<T, ?>... joins) {
        return new Query<>(mTable, mDistinct, joins, mProjection, mWhere, mOrderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    public Query<T> projection(@Nullable final String... projection) {
        return new Query<>(mTable, mDistinct, mJoins, projection, mWhere, mOrderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    public Query<T> where(@Nullable final Expression where) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, where, mOrderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    @Deprecated
    Query<T> where(@Nullable final Pair<String, String[]> where) {
        return where(where != null ? Expression.raw(where.first, where.second) : null);
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @NonNull final Object... args) {
        return where(where, bindValues(args));
    }

    @NonNull
    public Query<T> where(@Nullable final String where, @Nullable final String... args) {
        return where(where != null ? Expression.raw(where, args) : null);
    }

    @NonNull
    public Query<T> orderBy(@Nullable final String orderBy) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, orderBy, mGroupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    public Query<T> groupBy(@NonNull final Field... groupBy) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, groupBy, mHaving, mLimit, mOffset);
    }

    @NonNull
    public Query<T> having(@Nullable final Expression having) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, having, mLimit, mOffset);
    }

    @NonNull
    public Query<T> limit(@Nullable final Integer limit) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, mHaving, limit, mOffset);
    }

    @NonNull
    public Query<T> offset(@Nullable final Integer offset) {
        return new Query<>(mTable, mDistinct, mJoins, mProjection, mWhere, mOrderBy, mGroupBy, mHaving, mLimit, offset);
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

    @Nullable
    final String buildSqlLimit() {
        if (mLimit != null) {
            final StringBuilder sb = new StringBuilder();
//            // XXX: not supported by Android
//            // "{limit} OFFSET {offset}" syntax
//            sb.append(mLimit);
//            if (mOffset != null) {
//                sb.append(" OFFSET ").append(mOffset);
//            }

            // "{offset},{limit}" syntax
            if (mOffset != null) {
                sb.append(mOffset).append(',');
            }
            sb.append(mLimit);
            return sb.toString();
        }
        return null;
    }
}
