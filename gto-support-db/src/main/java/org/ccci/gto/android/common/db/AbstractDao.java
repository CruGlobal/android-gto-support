package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.Join.NO_JOINS;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao {
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
            } else {
                values[i] = raw[i].toString();
            }
        }
        return values;
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz) {
        return getCursor(clazz, null, null, null);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public final Cursor getCursor(@NonNull final Class<?> clazz, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(clazz, NO_JOINS, this.getFullProjection(clazz), whereClause, whereBindValues, orderBy);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final String[] projection,
                                  @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                  @Nullable final String orderBy) {
        return getCursor(clazz, NO_JOINS, projection, whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        //noinspection unchecked
        return getCursor(clazz, new Join[] {join}, projection, whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?> join1,
                                      @NonNull final Join<T, ?> join2, @NonNull final String[] projection,
                                      @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                      @Nullable final String orderBy) {
        //noinspection unchecked
        return getCursor(clazz, new Join[] {join1, join2}, projection, whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final <T> Cursor getCursor(@NonNull final Class<T> clazz, @NonNull final Join<T, ?>[] joins,
                                      @NonNull final String[] projection, @Nullable final String whereClause,
                                      @Nullable final String[] whereArgs, @Nullable String orderBy) {
        String[] args = null;

        // process joins
        final String tables;
        final String[] columns;
        if (joins.length > 0) {
            final String baseTable = getTable(clazz);

            // joins need to be passed appended to the table name
            final StringBuilder sb = new StringBuilder(baseTable);
            for (final Join<T, ?> joinObj : joins) {
                final Pair<String, String[]> join = joinObj.build(this);
                sb.append(join.first);
                args = ArrayUtils.merge(String.class, args, join.second);
            }
            tables = sb.toString();

            // prefix all non-prefixed columns in the projection to prevent ambiguous columns
            columns = new String[projection.length];
            for (int i = 0; i < projection.length; i++) {
                columns[i] = projection[i].contains(".") ? projection[i] : baseTable + "." + projection[i];
            }

            // prefix an un-prefixed orderBy field
            if (orderBy != null && !orderBy.contains(".")) {
                orderBy = baseTable + "." + orderBy;
            }
        } else {
            tables = getTable(clazz);
            columns = projection;
        }

        // add WHERE args
        args = ArrayUtils.merge(String.class, args, whereArgs);

        // execute actual query
        final Cursor c = mDbHelper.getReadableDatabase().query(tables, columns, whereClause, args, null, null, orderBy);

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
        return this.get(clazz, null, null, null);
    }

    @NonNull
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues) {
        return this.get(clazz, whereClause, whereBindValues, null);
    }

    @NonNull
    public final <T> List<T> get(@NonNull final Class<T> clazz, @Nullable final String whereClause,
                                 @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        // load all rows from the cursor
        final List<T> results = new ArrayList<>();
        final Cursor c = this.getCursor(clazz, whereClause, whereBindValues, orderBy);
        c.moveToPosition(-1);
        final Mapper<T> mapper = this.getMapper(clazz);
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
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(clazz, key);

        // return the first record if it exists
        Cursor c = null;
        try {
            c = this.getCursor(clazz, where.first, where.second, null);
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

    public final void insert(@NonNull final Object obj) {
        this.insert(obj, SQLiteDatabase.CONFLICT_NONE);
    }

    public final <T> void insert(@NonNull final T obj, final int conflictAlgorithm) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // execute insert
            db.insertWithOnConflict(this.getTable(clazz), null,
                                    this.getMapper(clazz).toContentValues(obj, this.getFullProjection(clazz)),
                                    conflictAlgorithm);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void replace(@NonNull final Object obj) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            this.delete(obj);
            this.insert(obj);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void update(@NonNull final Object obj) {
        this.update(obj, this.getFullProjection(obj.getClass()));
    }

    public final <T> void update(@NonNull final T obj, @NonNull final String[] projection) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);
            db.update(this.getTable(clazz), this.getMapper(clazz).toContentValues(obj, projection), where.first,
                      where.second);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void updateOrInsert(@NonNull final Object obj) {
        this.updateOrInsert(obj, this.getFullProjection(obj.getClass()));
    }

    public final void updateOrInsert(@NonNull final Object obj, @NonNull final String[] projection) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);
            final Object existing = this.find(obj.getClass(), (Object[]) where.second);
            if (existing != null) {
                this.update(obj, projection);
            } else {
                this.insert(obj);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void delete(@NonNull final Object obj) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);
            db.delete(this.getTable(obj.getClass()), where.first, where.second);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
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
