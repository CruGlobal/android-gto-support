package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.util.ArrayUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;

public abstract class AbstractDao extends AbstractDao2 {
    protected AbstractDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }

    @NonNull
    @WorkerThread
    public final Cursor getCursor(@NonNull final Class<?> clazz) {
        return getCursor(Query.select(clazz));
    }

    @NonNull
    @WorkerThread
    public final Cursor getCursor(@NonNull final Class<?> clazz, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(Query.select(clazz).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
    @Override
    @WorkerThread
    public final Cursor getCursor(@NonNull final Query<?> query) {
        // prefix projection and orderBy when we have joins
        String[] projection = query.mProjection != null ? query.mProjection : getFullProjection(query.mTable.mType);
        String orderBy = query.mOrderBy;

        if (query.mJoins.length > 0) {
            final String prefix = query.mTable.sqlPrefix(this);

            // prefix all non-prefixed columns in the projection to prevent ambiguous columns
            projection = projection.clone();
            for (int i = 0; i < projection.length; i++) {
                projection[i] = projection[i].contains(".") ? projection[i] : prefix + projection[i];
            }

            // prefix un-prefixed clauses
            orderBy = addPrefixToFields(orderBy, prefix);
        }

        // generate "FROM {}" SQL
        final Pair<String, String[]> from = query.buildSqlFrom(this);
        final String tables = from.first;
        String[] args = from.second;

        // generate "WHERE {}" SQL
        final Pair<String, String[]> where = query.buildSqlWhere(this);
        args = ArrayUtils.merge(String.class, args, where.second);

        // handle GROUP BY {} HAVING {}
        String groupBy = null;
        String having = null;
        if (query.mGroupBy.length > 0) {
            // generate "GROUP BY {}" SQL
            final StringBuilder groupByBuilder = new StringBuilder();
            boolean firstTime = true;
            for (final Field field : query.mGroupBy) {
                if (!firstTime) {
                    groupByBuilder.append(',');
                }
                groupByBuilder.append(field.buildSql(this).first);
                firstTime = false;
            }
            groupBy = groupByBuilder.toString();

            // generate "HAVING {}" SQL
            final Pair<String, String[]> havingRaw = query.buildSqlHaving(this);
            having = havingRaw.first;
            args = ArrayUtils.merge(String.class, args, havingRaw.second);
        }

        // generate "LIMIT {}" SQL
        final String limit = query.buildSqlLimit();

        // execute actual query
        final SQLiteDatabase db = getReadableDatabase();
        final Transaction tx = newTransaction(db);
        final Cursor c;
        try {
            tx.beginTransactionNonExclusive();
            c = db.query(query.mDistinct, tables, projection, where.first, args, groupBy, having, orderBy, limit);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction().recycle();
        }

        c.moveToPosition(-1);
        return c;
    }

    String addPrefixToFields(String clause, String prefix) {
        if (clause != null) {
            // If there is more than one field in the clause, add the prefix to each field
            if (clause.contains(",")) {
                String[] fields = clause.split(",");

                for (int i = 0; i < fields.length; i++) {
                    if (!fields[i].contains(".")) {
                        fields[i] = prefix + fields[i].trim();
                    }
                }
                return TextUtils.join(",", fields);
            }
            if (!clause.contains(".")) {
                return prefix + clause;
            }
        }
        return null;
    }

    @WorkerThread
    public final void replace(@NonNull final Object obj) {
        inNonExclusiveTransaction(() -> {
            delete(obj);
            insert(obj);
            return null;
        });
    }

    @WorkerThread
    public final int update(@NonNull final Object obj) {
        return update(obj, getFullProjection(obj.getClass()));
    }

    @WorkerThread
    public final <T> int update(@NonNull final T obj, @NonNull final String... projection) {
        return update(obj, CONFLICT_NONE, projection);
    }

    @WorkerThread
    public final <T> int update(@NonNull final T obj, final int conflictAlgorithm,
                                @NonNull final String... projection) {
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) obj.getClass();
        final ContentValues values = getMapper(type).toContentValues(obj, projection);
        return update(type, values, getPrimaryKeyWhere(obj), conflictAlgorithm);
    }

    @WorkerThread
    public final void updateOrInsert(@NonNull final Object obj) {
        updateOrInsert(obj, getFullProjection(obj.getClass()));
    }

    @WorkerThread
    public final void updateOrInsert(@NonNull final Object obj, @NonNull final String... projection) {
        updateOrInsert(obj, CONFLICT_NONE, projection);
    }

    @WorkerThread
    public final void updateOrInsert(@NonNull final Object obj, final int conflictAlgorithm,
                                     @NonNull final String... projection) {
        inNonExclusiveTransaction(() -> {
            final Object existing = refresh(obj);
            if (existing != null) {
                update(obj, conflictAlgorithm, projection);
            } else {
                insert(obj, conflictAlgorithm);
            }
            return null;
        });
    }

    @WorkerThread
    public final void delete(@NonNull final Object obj) {
        delete(obj.getClass(), getPrimaryKeyWhere(obj));
    }

    /**
     * Delete all objects that match the provided where clause. Sending a null where clause will delete all objects.
     *
     * @param clazz The Class of the objects to be deleted
     * @param where An expression describing which objects to delete. Null indicates all objects should be deleted.
     */
    @WorkerThread
    public final void delete(@NonNull final Class<?> clazz, @Nullable final Expression where) {
        final String table = getTable(clazz);
        final Pair<String, String[]> builtWhere =
                where != null ? where.buildSql(this) : Pair.<String, String[]>create(null, null);
        final SQLiteDatabase db = getWritableDatabase();
        inNonExclusiveTransaction(db, () -> db.delete(table, builtWhere.first, builtWhere.second));
    }

    @NonNull
    protected final Pair<String, String[]> compileExpression(@NonNull final Expression expression) {
        return expression.buildSql(this);
    }
}
