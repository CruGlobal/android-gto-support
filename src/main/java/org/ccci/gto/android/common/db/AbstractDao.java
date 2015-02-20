package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDao {
    @NonNull
    protected final SQLiteOpenHelper dbHelper;
    @NonNull
    private final Executor asyncExecutor;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected AbstractDao(@NonNull final SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.asyncExecutor = Executors.newFixedThreadPool(1);
        if(this.asyncExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) this.asyncExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                ((ThreadPoolExecutor) this.asyncExecutor).allowCoreThreadTimeOut(true);
            }
        }
    }

    @NonNull
    protected String getTable(@NonNull final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    protected String[] getFullProjection(@NonNull final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + clazz.getName());
    }

    @NonNull
    protected String getJoin(@NonNull final Class<?> base, @Nullable final String type, @NonNull final Class<?> join) {
        throw new IllegalArgumentException("unsupported join");
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
                values[i] = null;
            } else if (raw[i] instanceof Boolean) {
                values[i] = ((Boolean)raw[i]) ? "1" : "0";
            } else {
                values[i] = raw[i].toString();
            }
        }
        return values;
    }

    @Deprecated
    protected final String[] getBindValues(@NonNull final Object... raw) {
        return bindValues(raw);
    }

    @NonNull
    public final String buildJoin(@NonNull final Class<?> clazz) {
        return this.buildJoin(clazz, null, null);
    }

    @NonNull
    public final String buildJoin(@NonNull final Class<?> clazz, @Nullable final String type) {
        return this.buildJoin(clazz, type, null);
    }

    @NonNull
    public final String buildJoin(@NonNull final Class<?> clazz, @Nullable final String type,
                                  @Nullable final String on) {
        final StringBuilder sb =
                new StringBuilder(32 + (type != null ? type.length() + 1 : 0) + (on != null ? on.length() + 4 : 0));
        if (type != null) {
            sb.append(" ").append(type);
        }
        sb.append(" JOIN ").append(this.getTable(clazz));
        if (on != null) {
            sb.append(" ON ").append(on);
        }
        return sb.toString();
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz) {
        return getCursor(clazz, null, null, null);
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        return getCursor(clazz, (String[]) null, this.getFullProjection(clazz), whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final String[] projection,
                                  @Nullable final String whereClause, @Nullable final String[] whereBindValues,
                                  @Nullable final String orderBy) {
        return getCursor(clazz, (String[]) null, projection, whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz, @NonNull final Pair<String, Class<?>>[] joins,
                                  @NonNull final String[] projection, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        // process joins
        final String[] rawJoins = new String[joins.length];
        for (int i = 0; i < joins.length; i++) {
            rawJoins[i] = this.getJoin(clazz, joins[i].first, joins[i].second);
        }

        return getCursor(clazz, rawJoins, projection, whereClause, whereBindValues, orderBy);
    }

    @NonNull
    public final Cursor getCursor(@NonNull final Class<?> clazz, @Nullable final String[] joins,
                                  @NonNull final String[] projection, @Nullable final String whereClause,
                                  @Nullable final String[] whereBindValues, @Nullable final String orderBy) {
        final String table = this.getTable(clazz) + (joins != null ? TextUtils.join(" ", joins) : "");

        final Cursor c = this.dbHelper.getReadableDatabase()
                .query(table, projection, whereClause, whereBindValues, null, null, orderBy);

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

        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
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
        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
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

        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
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
        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
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
        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final Pair<String, String[]> where = this.getPrimaryKeyWhere(obj);
            db.delete(this.getTable(obj.getClass()), where.first, where.second);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void async(@NonNull final Runnable task) {
        this.asyncExecutor.execute(task);
    }

    @NonNull
    public final Transaction newTransaction() {
        return new Transaction(this.dbHelper.getWritableDatabase());
    }

    @NonNull
    public final Transaction beginTransaction() {
        return newTransaction().beginTransaction();
    }

    public static final class Transaction {
        private final static int STATE_INIT = 0;
        private final static int STATE_OPEN = 1;
        private final static int STATE_SUCCESSFUL = 2;
        private final static int STATE_CLOSED = 3;

        private final SQLiteDatabase db;
        private int state = STATE_INIT;

        private Transaction(@NonNull final SQLiteDatabase db) {
            this.db = db;
        }

        @NonNull
        public synchronized Transaction begin() {
            return this.beginTransaction();
        }

        @NonNull
        public synchronized Transaction beginTransaction() {
            if (this.state < STATE_OPEN) {
                this.db.beginTransaction();
                this.state = STATE_OPEN;
            }

            return this;
        }

        @NonNull
        public synchronized Transaction setSuccessful() {
            return this.setTransactionSuccessful();
        }

        @NonNull
        public synchronized Transaction setTransactionSuccessful() {
            if (this.state >= STATE_OPEN && this.state < STATE_SUCCESSFUL) {
                this.db.setTransactionSuccessful();
                this.state = STATE_SUCCESSFUL;
            }

            return this;
        }

        @NonNull
        public synchronized Transaction end() {
            return this.endTransaction();
        }

        @NonNull
        public synchronized Transaction endTransaction() {
            if (this.state >= STATE_OPEN && this.state < STATE_CLOSED) {
                this.db.endTransaction();
                this.state = STATE_CLOSED;
            }

            return this;
        }
    }
}
