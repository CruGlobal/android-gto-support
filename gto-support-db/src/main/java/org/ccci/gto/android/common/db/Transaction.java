package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Not Thread-safe, this object is designed to be used on a single-thread only
 */
public final class Transaction {
    private static final int STATE_INIT = 0;
    private static final int STATE_OPEN = 1;
    private static final int STATE_SUCCESSFUL = 2;
    private static final int STATE_CLOSED = 3;

    private final SQLiteDatabase mDb;
    private int mState = STATE_INIT;

    public Transaction(@NonNull final SQLiteDatabase db) {
        mDb = db;
    }

    @NonNull
    public static Transaction begin(@NonNull final SQLiteDatabase db) {
        return new Transaction(db).beginTransaction(true);
    }

    @NonNull
    public Transaction begin() {
        return beginTransaction(true);
    }

    @NonNull
    public Transaction beginTransaction() {
        return beginTransaction(true);
    }

    /**
     * Starts a non-exclusive transaction. Gracefully falls back to an exclusive transaction on pre-Honeycomb
     *
     * @return this Transaction object
     */
    @NonNull
    public Transaction beginTransactionNonExclusive() {
        return beginTransaction(false);
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Transaction beginTransaction(final boolean exclusive) {
        if (mState < STATE_OPEN) {
            if (exclusive || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mDb.beginTransaction();
            } else {
                mDb.beginTransactionNonExclusive();
            }
            mState = STATE_OPEN;
        }

        return this;
    }

    @NonNull
    public Transaction setSuccessful() {
        return setTransactionSuccessful();
    }

    @NonNull
    public Transaction setTransactionSuccessful() {
        if (mState >= STATE_OPEN && mState < STATE_SUCCESSFUL) {
            mDb.setTransactionSuccessful();
            mState = STATE_SUCCESSFUL;
        }

        return this;
    }

    @NonNull
    public Transaction end() {
        return endTransaction();
    }

    @NonNull
    public Transaction endTransaction() {
        if (mState >= STATE_OPEN && mState < STATE_CLOSED) {
            mDb.endTransaction();
            mState = STATE_CLOSED;
        }

        return this;
    }
}
