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
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;
import org.ccci.gto.android.common.util.LocaleCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class AbstractDao {
    public static final String ARG_DISTINCT = AbstractDao.class.getName() + ".ARG_DISTINCT";
    public static final String ARG_JOINS = AbstractDao.class.getName() + ".ARG_JOINS";
    public static final String ARG_PROJECTION = AbstractDao.class.getName() + ".ARG_PROJECTION";
    public static final String ARG_WHERE = AbstractDao.class.getName() + ".ARG_WHERE";
    @Deprecated
    public static final String ARG_WHERE_ARGS = AbstractDao.class.getName() + ".ARG_WHERE_ARGS";
    public static final String ARG_ORDER_BY = AbstractDao.class.getName() + ".ARG_ORDER_BY";

    @NonNull
    private final SQLiteOpenHelper mDbHelper;
    private final SimpleArrayMap<Class<?>, TableType> mTableTypes = new SimpleArrayMap<>();

    protected AbstractDao(@NonNull final SQLiteOpenHelper helper) {
        mDbHelper = helper;
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
     * @deprecated override {@link AbstractDao#getPrimaryKeyWhere(Class)} instead.
     */
    @NonNull
    @Deprecated
    protected Pair<String, String[]> getPrimaryKeyWhereRaw(@NonNull final Class<?> clazz,
                                                           @NonNull final Object... key) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    /**
     * @deprecated override {@link AbstractDao#getPrimaryKeyWhere(Object)} instead.
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

    @NonNull
    @Deprecated
    @WorkerThread
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final String[] projection,
                                  @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                  @Nullable final String orderBy) {
        return getCursor(
                Query.select(clazz).projection(projection).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
    @Deprecated
    @WorkerThread
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(Query.select(clazz).join(join).projection(projection).where(whereClause, whereBindValues)
                                 .orderBy(orderBy));
    }

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
    public final <T> Cursor getCursor(@NonNull final Query<T> query) {
        // prefix projection and orderBy when we have joins
        String[] projection = query.mProjection != null ? query.mProjection : getFullProjection(query.mTable.mType);
        String orderBy = query.mOrderBy;
        String groupBy = query.mGroupBy;
        if (query.mJoins.length > 0) {
            final String prefix = query.mTable.sqlPrefix(this);

            // prefix all non-prefixed columns in the projection to prevent ambiguous columns
            projection = projection.clone();
            for (int i = 0; i < projection.length; i++) {
                projection[i] = projection[i].contains(".") ? projection[i] : prefix + projection[i];
            }

            // prefix an un-prefixed orderBy field
            if (orderBy != null && !orderBy.contains(".")) {
                orderBy = prefix + orderBy;
            }

            if(groupBy != null && !groupBy.contains(".")) {
                groupBy = prefix + groupBy;
            }
        }

        // generate "FROM {}" SQL
        final Pair<String, String[]> from = query.buildSqlFrom(this);
        final String tables = from.first;
        String[] args = from.second;

        // generate "WHERE {}" SQL
        final Pair<String, String[]> where = query.buildSqlWhere(this);
        args = ArrayUtils.merge(String.class, args, where.second);

        // execute actual query
        final Cursor c = mDbHelper.getReadableDatabase()
                .query(query.mDistinct, tables, projection, where.first, args, groupBy, null, orderBy, null);

        c.moveToPosition(-1);
        return c;
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
        this.update(obj, this.getFullProjection(obj.getClass()));
    }

    @WorkerThread
    public final <T> void update(@NonNull final T obj, @NonNull final String... projection) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String table = this.getTable(clazz);
        final ContentValues values = this.getMapper(clazz).toContentValues(obj, projection);
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj).buildSql(this);

        // execute update
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            db.update(table, values, where.first, where.second);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
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
    public final void delete(@NonNull final Class<?> clazz, @NonNull final Expression where) {
        final String table = this.getTable(clazz);
        final Pair<String, String[]> builtWhere = where.buildSql(this);
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

    @WorkerThread
    public final void delete(@NonNull final Object obj) {
        final String table = this.getTable(obj.getClass());
        final Pair<String, String[]> where = getPrimaryKeyWhere(obj).buildSql(this);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            db.delete(table, where.first, where.second);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
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

        public TableType(@NonNull final String table, @Nullable final String[] projection,
                         @Nullable final Mapper<?> mapper, @Nullable final Expression where) {
            mTable = table;
            mProjection = projection;
            mMapper = mapper;
            mPrimaryWhere = where;
        }
    }
}
