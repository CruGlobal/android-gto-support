package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.db.CommonTables.LastSyncTable;
import org.ccci.gto.android.common.db.Expression.Field;
import org.ccci.gto.android.common.util.ArrayUtils;
import org.ccci.gto.android.common.util.LocaleCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.ccci.gto.android.common.db.util.CursorUtils.getLong;

public abstract class AbstractDao {
    public static final String ARG_DISTINCT = AbstractDao.class.getName() + ".ARG_DISTINCT";
    public static final String ARG_JOINS = AbstractDao.class.getName() + ".ARG_JOINS";
    public static final String ARG_PROJECTION = AbstractDao.class.getName() + ".ARG_PROJECTION";
    public static final String ARG_WHERE = AbstractDao.class.getName() + ".ARG_WHERE";
    /**
     * @deprecated Since v0.9.0, use {@link AbstractDao#ARG_WHERE} with an {@link Expression} object instead.
     */
    @Deprecated
    public static final String ARG_WHERE_ARGS = AbstractDao.class.getName() + ".ARG_WHERE_ARGS";
    public static final String ARG_ORDER_BY = AbstractDao.class.getName() + ".ARG_ORDER_BY";

    @NonNull
    private final SQLiteOpenHelper mDbHelper;
    private final SimpleArrayMap<Class<?>, TableType> mTableTypes = new SimpleArrayMap<>();

    protected AbstractDao(@NonNull final SQLiteOpenHelper helper) {
        mDbHelper = helper;

        registerType(LastSyncTable.class, LastSyncTable.TABLE_NAME, null, null, LastSyncTable.SQL_WHERE_PRIMARY_KEY);
    }

    @WorkerThread
    protected final SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }

    @WorkerThread
    protected final SQLiteDatabase getWritableDatabase() {
        return mDbHelper.getWritableDatabase();
    }

    protected final <T> void registerType(@NonNull final Class<T> clazz, @NonNull final String table,
                                          @Nullable final String[] projection, @Nullable final Mapper<T> mapper,
                                          @Nullable final Expression pkWhere) {
        mTableTypes.put(clazz, new TableType(table, projection, mapper, pkWhere));
    }

    @NonNull
    protected String getTable(@NonNull final Class<?> clazz) {
        // check for a registered type
        final TableType type = mTableTypes.get(clazz);
        if (type != null) {
            return type.mTable;
        }

        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    public String[] getFullProjection(@NonNull final Class<?> clazz) {
        // check for a registered type
        final TableType type = mTableTypes.get(clazz);
        if (type != null && type.mProjection != null) {
            return type.mProjection;
        }

        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    public final String[] getFullProjection(@NonNull final Table<?> table) {
        return getFullProjection(table.mType);
    }

    @NonNull
    protected Expression getPrimaryKeyWhere(@NonNull final Class<?> clazz) {
        // check for a registered type
        final TableType type = mTableTypes.get(clazz);
        if (type != null && type.mPrimaryWhere != null) {
            return type.mPrimaryWhere;
        }

        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    protected Expression getPrimaryKeyWhere(@NonNull final Class<?> clazz, @NonNull final Object... key) {
        try {
            return getPrimaryKeyWhere(clazz).args(key);
        } catch (final IllegalArgumentException e) {
            // try fallback to legacy getPrimaryKeyWhereRaw() method
            try {
                final Pair<String, String[]> raw = getPrimaryKeyWhereRaw(clazz, key);
                return Expression.raw(raw.first, raw.second);
            } catch (final Throwable e2) {
                // add this as a suppressed exception in case it has additional information
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    e.addSuppressed(e2);
                }
                throw e;
            }
        }
    }

    @NonNull
    protected Expression getPrimaryKeyWhere(@NonNull final Object obj) {
        // fallback to legacy getPrimaryKeyWhereRaw() method
        final Pair<String, String[]> raw = getPrimaryKeyWhereRaw(obj);
        return Expression.raw(raw.first, raw.second);
    }

    /**
     * @deprecated Since v0.8.0, override {@link AbstractDao#getPrimaryKeyWhere(Class)} instead.
     */
    @NonNull
    @Deprecated
    protected Pair<String, String[]> getPrimaryKeyWhereRaw(@NonNull final Class<?> clazz,
                                                           @NonNull final Object... key) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    /**
     * @deprecated Since v0.8.0, override {@link AbstractDao#getPrimaryKeyWhere(Object)} instead.
     */
    @NonNull
    @Deprecated
    protected Pair<String, String[]> getPrimaryKeyWhereRaw(@NonNull final Object obj) {
        throw new IllegalArgumentException("unsupported object: " + obj.getClass());
    }

    @NonNull
    @SuppressWarnings("unchecked")
    protected <T> Mapper<T> getMapper(@NonNull final Class<T> clazz) {
        // check for a registered type
        final TableType type = mTableTypes.get(clazz);
        if (type != null && type.mMapper != null) {
            return (Mapper<T>) type.mMapper;
        }

        throw new IllegalArgumentException("invalid class specified");
    }

    @NonNull
    public static String[] bindValues(@NonNull final Object... raw) {
        final String[] values = new String[raw.length];
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] == null) {
                throw new IllegalArgumentException("Bind Values cannot be null");
            } else if (raw[i] instanceof Boolean) {
                values[i] = ((Boolean) raw[i]) ? "1" : "0";
            } else if (raw[i] instanceof Date) {
                values[i] = Long.toString(((Date) raw[i]).getTime());
            } else if (raw[i] instanceof Locale) {
                values[i] = LocaleCompat.toLanguageTag((Locale) raw[i]);
            } else {
                values[i] = raw[i].toString();
            }
        }
        return values;
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

    /**
     * @deprecated Since v0.9.0, use {@link AbstractDao#getCursor(Query)} instead.
     */
    @NonNull
    @Deprecated
    @WorkerThread
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final String[] projection,
                                  @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                  @Nullable final String orderBy) {
        return getCursor(
                Query.select(clazz).projection(projection).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    /**
     * @deprecated Since v0.9.0, use {@link AbstractDao#getCursor(Query)} instead.
     */
    @NonNull
    @Deprecated
    @WorkerThread
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(Query.select(clazz).join(join).projection(projection).where(whereClause, whereBindValues)
                                 .orderBy(orderBy));
    }

    /**
     * @deprecated Since v0.9.0, use {@link AbstractDao#getCursor(Query)} instead.
     */
    @NonNull
    @Deprecated
    @WorkerThread
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join1,
                                      @NonNull final Join<T, ?> join2, @NonNull final String[] projection,
                                      @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                      @Nullable final String orderBy) {
        return getCursor(
                Query.select(clazz).join(join1, join2).projection(projection).where(whereClause, whereBindValues)
                        .orderBy(orderBy));
    }

    /**
     * @deprecated Since v0.9.0, use {@link AbstractDao#getCursor(Query)} instead.
     */
    @NonNull
    @Deprecated
    @WorkerThread
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?>[] joins,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereArgs, @Nullable String orderBy) {
        return getCursor(
                Query.select(clazz).joins(joins).projection(projection).where(whereClause, whereArgs).orderBy(orderBy));
    }

    @NonNull
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
        final Cursor c = mDbHelper.getReadableDatabase()
                .query(query.mDistinct, tables, projection, where.first, args, groupBy, having, orderBy, limit);

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

    /**
     * retrieve all objects of the specified type
     *
     * @param clazz the type of object to retrieve
     * @return
     */
    @NonNull
    @WorkerThread
    public final <T> List<T> get(@NonNull final Class<T> clazz) {
        return get(Query.select(clazz));
    }

    @NonNull
    @WorkerThread
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues) {
        return get(Query.select(clazz).where(whereClause, whereBindValues));
    }

    @NonNull
    @WorkerThread
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return get(Query.select(clazz).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
    @WorkerThread
    public final <T> List<T> get(@NonNull final Query<T> query) {
        // load all rows from the cursor
        final List<T> results = new ArrayList<>();
        final Cursor c = getCursor(query.projection());
        c.moveToPosition(-1);
        final Mapper<T> mapper = getMapper(query.mTable.mType);
        while (c.moveToNext()) {
            results.add(mapper.toObject(c));
        }

        // close the cursor to prevent leaking it
        c.close();

        // return the results
        return results;
    }

    @Nullable
    @WorkerThread
    public final <T> T find(@NonNull final Class<T> clazz, @NonNull final Object... key) {
        return find(clazz, getPrimaryKeyWhere(clazz, key));
    }

    @Nullable
    @WorkerThread
    private <T> T find(@NonNull final Class<T> clazz, @NonNull final Expression where) {
        // return the first record if it exists
        Cursor c = null;
        try {
            c = getCursor(Query.select(clazz).where(where));
            if (c.getCount() > 0) {
                c.moveToFirst();
                return this.getMapper(clazz).toObject(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        // default to null
        return null;
    }

    @Nullable
    @WorkerThread
    @SuppressWarnings("unchecked")
    public final <T> T refresh(@NonNull T obj) {
        return find((Class<T>) obj.getClass(), getPrimaryKeyWhere(obj));
    }

    @WorkerThread
    public final long insert(@NonNull final Object obj) {
        return insert(obj, SQLiteDatabase.CONFLICT_NONE);
    }

    @WorkerThread
    public final <T> long insert(@NonNull final T obj, final int conflictAlgorithm) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String table = getTable(clazz);
        final ContentValues values = getMapper(clazz).toContentValues(obj, this.getFullProjection(clazz));

        // execute insert
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            final long id = db.insertWithOnConflict(table, null, values, conflictAlgorithm);
            tx.setTransactionSuccessful();
            return id;
        } finally {
            tx.endTransaction();
        }
    }

    @WorkerThread
    public final void replace(@NonNull final Object obj) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            this.delete(obj);
            this.insert(obj);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
    }

    @WorkerThread
    public final void update(@NonNull final Object obj) {
        update(obj, getFullProjection(obj.getClass()));
    }

    @WorkerThread
    public final <T> void update(@NonNull final T obj, @NonNull final String... projection) {
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) obj.getClass();
        final ContentValues values = getMapper(type).toContentValues(obj, projection);
        update(type, values, getPrimaryKeyWhere(obj));
    }

    /**
     * Update the specified {@code values} for objects of type {@code type} that match the specified {@code where}
     * clause. If {@code where} is null, all objects of type {@code type} will be updated
     *
     * @param type   the type of Object to update
     * @param values the new values for the specified object
     * @param where  an optional {@link Expression} to narrow the scope of which objects are updated
     */
    @WorkerThread
    protected final void update(@NonNull final Class<?> type, @NonNull final ContentValues values,
                                @Nullable final Expression where) {
        final String table = getTable(type);
        final Pair<String, String[]> builtWhere =
                where != null ? where.buildSql(this) : Pair.<String, String[]>create(null, null);

        // execute update
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            db.update(table, values, builtWhere.first, builtWhere.second);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
    }

    /**
     * This method updates all objects of type {@code type} in the database with the {@code projection} values from the
     * {@code sample} object. The objects actually updated can be restricted via the {@code where} expression.
     *
     * @param type       the type of objects being updated
     * @param where      a where clause that restricts which objects get updated. If this is null all objects are
     *                   updated.
     * @param sample     a sample object that is used to generated the values being set on other objects.
     * @param projection the fields to update in this call
     * @param <T>        the type of objects being updated
     */
    @WorkerThread
    public final <T> void updateAll(@NonNull final Class<T> type, @Nullable final Expression where,
                                    @NonNull final T sample, @NonNull final String... projection) {
        update(type, getMapper(type).toContentValues(sample, projection), where);
    }

    @WorkerThread
    public final void updateOrInsert(@NonNull final Object obj) {
        this.updateOrInsert(obj, this.getFullProjection(obj.getClass()));
    }

    @WorkerThread
    public final void updateOrInsert(@NonNull final Object obj, @NonNull final String... projection) {
        final Expression where = this.getPrimaryKeyWhere(obj);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            final Object existing = find(obj.getClass(), where);
            if (existing != null) {
                this.update(obj, projection);
            } else {
                this.insert(obj);
            }
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
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
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            db.delete(table, builtWhere.first, builtWhere.second);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
    }

    public long getLastSyncTime(@NonNull final Object... key) {
        final Cursor c =
                getCursor(Query.select(LastSyncTable.class).projection(LastSyncTable.COLUMN_LAST_SYNCED)
                                  .where(LastSyncTable.SQL_WHERE_PRIMARY_KEY.args(TextUtils.join(":", key))));
        if (c.moveToFirst()) {
            return getLong(c, LastSyncTable.COLUMN_LAST_SYNCED, 0L);
        }
        return 0;
    }

    public void updateLastSyncTime(@NonNull final Object... key) {
        // update the last sync time, we can replace since this is just a keyed timestamp
        final ContentValues values = new ContentValues();
        values.put(LastSyncTable.COLUMN_KEY, TextUtils.join(":", key));
        values.put(LastSyncTable.COLUMN_LAST_SYNCED, System.currentTimeMillis());
        getWritableDatabase().replace(getTable(LastSyncTable.class), null, values);
    }

    @NonNull
    protected final Pair<String, String[]> compileExpression(@NonNull final Expression expression) {
        return expression.buildSql(this);
    }

    @NonNull
    @WorkerThread
    public final Transaction newTransaction() {
        return new Transaction(mDbHelper.getWritableDatabase());
    }

    @NonNull
    @WorkerThread
    public final Transaction beginTransaction() {
        return newTransaction().beginTransaction();
    }

    private static final class TableType {
        @NonNull
        final String mTable;
        @Nullable
        final String[] mProjection;
        @Nullable
        final Mapper<?> mMapper;
        @Nullable
        final Expression mPrimaryWhere;

        TableType(@NonNull final String table, @Nullable final String[] projection, @Nullable final Mapper<?> mapper,
                  @Nullable final Expression where) {
            mTable = table;
            mProjection = projection;
            mMapper = mapper;
            mPrimaryWhere = where;
        }
    }
}
