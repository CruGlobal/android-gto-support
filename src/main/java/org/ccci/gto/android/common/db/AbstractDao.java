package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDao {
    protected final SQLiteOpenHelper dbHelper;
    private final Executor asyncExecutor;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected AbstractDao(final SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.asyncExecutor = Executors.newFixedThreadPool(1);
        if(this.asyncExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) this.asyncExecutor).setKeepAliveTime(30, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                ((ThreadPoolExecutor) this.asyncExecutor).allowCoreThreadTimeOut(true);
            }
        }
    }

    protected String getTable(final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + (clazz != null ? clazz.getName() : null));
    }

    protected String[] getFullProjection(final Class<?> clazz) {
        throw new IllegalArgumentException("invalid class specified: " + (clazz != null ? clazz.getName() : null));
    }

    protected String getJoin(final Class<?> base, final String type, final Class<?> join) {
        throw new IllegalArgumentException("unsupported join");
    }

    protected Pair<String, String[]> getPrimaryKeyWhere(final Class<?> clazz, final Object... key) {
        throw new IllegalArgumentException("invalid class specified: " + (clazz != null ? clazz.getName() : null));
    }

    protected Pair<String, String[]> getPrimaryKeyWhere(final Object obj) {
        throw new IllegalArgumentException("unsupported object");
    }

    protected <T> Mapper<T> getMapper(final Class<T> clazz) {
        throw new IllegalArgumentException("invalid class specified");
    }

    protected String[] getBindValues(final Object... raw) {
        final String[] values = new String[raw.length];
        for (int i = 0; i < raw.length; i++) {
            if(raw[i] instanceof Boolean) {
                values[i] = ((Boolean)raw[i]) ? "1" : "0";
            } else {
                values[i] = raw[i].toString();
            }
        }
        return values;
    }

    public final String buildJoin(final Class<?> clazz) {
        return this.buildJoin(clazz, null, null);
    }

    public final String buildJoin(final Class<?> clazz, final String type) {
        return this.buildJoin(clazz, type, null);
    }

    public final String buildJoin(final Class<?> clazz, final String type, final String on) {
        final StringBuilder sb = new StringBuilder(32 + (type != null ? type.length() + 1 : 0) +
                                                           (on != null ? on.length() + 4 : 0));
        if (type != null) {
            sb.append(" ").append(type);
        }
        sb.append(" JOIN ").append(this.getTable(clazz));
        if (on != null) {
            sb.append(" ON ").append(on);
        }
        return sb.toString();
    }

    public final Cursor getCursor(final Class<?> clazz) {
        return getCursor(clazz, null, null, null);
    }

    public final Cursor getCursor(final Class<?> clazz, final String whereClause, final String[] whereBindValues,
                            final String orderBy) {
        return getCursor(clazz, (String[]) null, this.getFullProjection(clazz), whereClause, whereBindValues, orderBy);
    }

    public final Cursor getCursor(final Class<?> clazz, final String[] projection, final String whereClause,
                            final String[] whereBindValues, final String orderBy) {
        return getCursor(clazz, (String[]) null, projection, whereClause, whereBindValues, orderBy);
    }

    public final Cursor getCursor(final Class<?> clazz, final Pair<String, Class<?>>[] joins, final String[] projection,
                            final String whereClause, final String[] whereBindValues, final String orderBy) {
        // process joins
        final String[] rawJoins = new String[joins.length];
        for (int i = 0; i < joins.length; i++) {
            rawJoins[i] = this.getJoin(clazz, joins[i].first, joins[i].second);
        }

        return getCursor(clazz, rawJoins, projection, whereClause, whereBindValues, orderBy);
    }

    public final Cursor getCursor(final Class<?> clazz, final String[] joins, final String[] projection,
                            final String whereClause, final String[] whereBindValues, final String orderBy) {
        final String table = this.getTable(clazz) + (joins != null ? TextUtils.join(" ", joins) : "");

        final Cursor c = this.dbHelper.getReadableDatabase().query(table, projection, whereClause,
                                                                   whereBindValues, null, null, orderBy);

        if (c != null) {
            c.moveToPosition(-1);
        }

        return c;
    }

    /**
     * retrieve all objects of the specified type
     *
     * @param clazz the type of object to retrieve
     * @return
     */
    public final <T> List<T> get(final Class<T> clazz) {
        return this.get(clazz, null, null, null);
    }

    public final <T> List<T> get(final Class<T> clazz, final String whereClause, final String[] whereBindValues) {
        return this.get(clazz, whereClause, whereBindValues, null);
    }

    public final <T> List<T> get(final Class<T> clazz, final String whereClause, final String[] whereBindValues,
                           final String orderBy) {
        // load all rows from the cursor
        final List<T> results = new ArrayList<T>();
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

    public final <T> T find(final Class<T> clazz, final Object... key) {
        final Pair<String, String[]> where = this.getPrimaryKeyWhere(clazz, key);

        // return the first record if it exists
        Cursor c = null;
        try {
            c = this.getCursor(clazz, where.first, where.second, null);
            if (c != null && c.getCount() > 0) {
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

    public final void insert(final Object obj) {
        this.insert(obj, SQLiteDatabase.CONFLICT_NONE);
    }

    public final <T> void insert(final T obj, final int conflictAlgorithm) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();

        final SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // execute insert
            db.insertWithOnConflict(this.getTable(clazz), null, this.getMapper(clazz).toContentValues(obj),
                                    conflictAlgorithm);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public final void replace(final Object obj) {
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

    public final void update(final Object obj) {
        this.update(obj, this.getFullProjection(obj.getClass()));
    }

    public final <T> void update(final T obj, final String[] projection) {
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

    public final void updateOrInsert(final Object obj, final String[] projection) {
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

    public final void delete(final Object obj) {
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

    public final void async(final Runnable task) {
        this.asyncExecutor.execute(task);
    }

    public final Transaction newTransaction() {
        return new Transaction(this.dbHelper.getWritableDatabase());
    }

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

        private Transaction(final SQLiteDatabase db) {
            this.db = db;
        }

        public synchronized Transaction begin() {
            return this.beginTransaction();
        }

        public synchronized Transaction beginTransaction() {
            if (this.state < STATE_OPEN) {
                this.db.beginTransaction();
                this.state = STATE_OPEN;
            }

            return this;
        }

        public synchronized Transaction setSuccessful() {
            return this.setTransactionSuccessful();
        }

        public synchronized Transaction setTransactionSuccessful() {
            if (this.state >= STATE_OPEN && this.state < STATE_SUCCESSFUL) {
                this.db.setTransactionSuccessful();
                this.state = STATE_SUCCESSFUL;
            }

            return this;
        }

        public synchronized Transaction end() {
            return this.endTransaction();
        }

        public synchronized Transaction endTransaction() {
            if (this.state >= STATE_OPEN && this.state < STATE_CLOSED) {
                this.db.endTransaction();
                this.state = STATE_CLOSED;
            }

            return this;
        }
    }
}
