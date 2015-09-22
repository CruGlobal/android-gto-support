package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;
import org.ccci.gto.android.common.util.LocaleCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractDao {
    public static final String ARG_DISTINCT = AbstractDao.class.getName() + ".ARG_DISTINCT";
    public static final String ARG_JOINS = AbstractDao.class.getName() + ".ARG_JOINS";
    public static final String ARG_PROJECTION = AbstractDao.class.getName() + ".ARG_PROJECTION";
    public static final String ARG_WHERE = AbstractDao.class.getName() + ".ARG_WHERE";
    public static final String ARG_WHERE_ARGS = AbstractDao.class.getName() + ".ARG_WHERE_ARGS";
    public static final String ARG_ORDER_BY = AbstractDao.class.getName() + ".ARG_ORDER_BY";

    @NonNull
    private final SQLiteOpenHelper mDbHelper;

    protected AbstractDao(@NonNull final SQLiteOpenHelper helper) {
        mDbHelper = helper;
    }

    protected final SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }

    protected final SQLiteDatabase getWritableDatabase() {
        return mDbHelper.getWritableDatabase();
    }

    @NonNull
    protected String getTable(@NonNull final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    public String[] getFullProjection(@NonNull final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    public final String[] getFullProjection(@NonNull final Table<?> table) {
        return getFullProjection(table.mType);
    }

    @NonNull
    protected Pair<String, String[]> getPrimaryKeyWhere(@NonNull final Class<?> clazz, @NonNull final Object... key) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    protected Pair<String, String[]> getPrimaryKeyWhere(@NonNull final Object obj) {
        throw new IllegalArgumentException("unsupported object");
    }

    @NonNull
    protected <T> Mapper<T> getMapper(@NonNull final Class<T> clazz) {
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
            } else if (raw[i] instanceof Locale) {
                values[i] = LocaleCompat.toLanguageTag((Locale) raw[i]);
            } else {
                values[i] = raw[i].toString();
            }
        }
        return values;
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz) {
        return getCursor(Query.select(clazz));
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(Query.select(clazz).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
    @Deprecated
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final String[] projection,
                                  @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                  @Nullable final String orderBy) {
        return getCursor(
                Query.select(clazz).projection(projection).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
    @Deprecated
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(Query.select(clazz).join(join).projection(projection).where(whereClause, whereBindValues)
                                 .orderBy(orderBy));
    }

    @NonNull
    @Deprecated
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
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?>[] joins,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereArgs, @Nullable String orderBy) {
        return getCursor(
                Query.select(clazz).joins(joins).projection(projection).where(whereClause, whereArgs).orderBy(orderBy));
    }

    @NonNull
    public final <T> Cursor getCursor(@NonNull final Query<T> query) {
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

            // prefix an un-prefixed orderBy field
            if (orderBy != null && !orderBy.contains(".")) {
                orderBy = prefix + orderBy;
            }
        }

        // generate "FROM {}" SQL
        final Pair<String, String[]> from = query.buildSqlFrom(this);
        final String tables = from.first;
        String[] args = from.second;

        // add WHERE args
        args = ArrayUtils.merge(String.class, args, query.mWhereArgs);

        // execute actual query
        final Cursor c = mDbHelper.getReadableDatabase()
                .query(query.mDistinct, tables, projection, query.mWhere, args, null, null, orderBy, null);

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
    public final <T> List<T> get(@NonNull final Class<T> clazz) {
        return get(Query.select(clazz));
    }

    @NonNull
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues) {
        return get(Query.select(clazz).where(whereClause, whereBindValues));
    }

    @NonNull
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return get(Query.select(clazz).where(whereClause, whereBindValues).orderBy(orderBy));
    }

    @NonNull
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
    public final <T> T find(@NonNull final Class<T> clazz, @NonNull final Object... key) {
        // return the first record if it exists
        Cursor c = null;
        try {
            c = getCursor(Query.select(clazz).where(getPrimaryKeyWhere(clazz, key)));
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
    @SuppressWarnings("unchecked")
    public final <T> T refresh(@NonNull T obj) {
        return find((Class<T>) obj.getClass(), getPrimaryKeyWhere(obj).second);
    }

    public final void insert(@NonNull final Object obj) {
        this.insert(obj, SQLiteDatabase.CONFLICT_NONE);
    }

    public final <T> void insert(@NonNull final T obj, final int conflictAlgorithm) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String table = getTable(clazz);
        final ContentValues values = getMapper(clazz).toContentValues(obj, this.getFullProjection(clazz));

        // execute insert
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            db.insertWithOnConflict(table, null, values, conflictAlgorithm);
            tx.setTransactionSuccessful();
        } finally {
            tx.endTransaction();
        }
    }

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

    public final void update(@NonNull final Object obj) {
        this.update(obj, this.getFullProjection(obj.getClass()));
    }

    public final <T> void update(@NonNull final T obj, @NonNull final String[] projection) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String table = this.getTable(clazz);
        final ContentValues values = this.getMapper(clazz).toContentValues(obj, projection);
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);

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

    public final void updateOrInsert(@NonNull final Object obj) {
        this.updateOrInsert(obj, this.getFullProjection(obj.getClass()));
    }

    public final void updateOrInsert(@NonNull final Object obj, @NonNull final String[] projection) {
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Transaction tx = new Transaction(db);
        try {
            tx.beginTransactionNonExclusive();
            final Object existing = find(obj.getClass(), where.second);
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

    public final void delete(@NonNull final Object obj) {
        final String table = this.getTable(obj.getClass());
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);

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
    public final Transaction newTransaction() {
        return new Transaction(mDbHelper.getWritableDatabase());
    }

    @NonNull
    public final Transaction beginTransaction() {
        return newTransaction().beginTransaction();
    }
}
