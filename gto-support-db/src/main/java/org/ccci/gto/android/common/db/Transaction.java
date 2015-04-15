package org.ccci.gto.android.common.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public class Transaction {
    private final static int STATE_INIT = 0;
    private final static int STATE_OPEN = 1;
    private final static int STATE_SUCCESSFUL = 2;
    private final static int STATE_CLOSED = 3;

    private final SQLiteDatabase mDb;
    private int mState = STATE_INIT;

    public Transaction(@NonNull final SQLiteDatabase db) {
        mDb = db;
    }

    @NonNull
    public synchronized Transaction begin() {
        return beginTransaction();
    }

    @NonNull
    public synchronized Transaction beginTransaction() {
        if (mState < STATE_OPEN) {
            mDb.beginTransaction();
            mState = STATE_OPEN;
        }

        return this;
    }

    @NonNull
    public synchronized Transaction setSuccessful() {
        return setTransactionSuccessful();
    }

    @NonNull
    public synchronized Transaction setTransactionSuccessful() {
        if (mState >= STATE_OPEN && mState < STATE_SUCCESSFUL) {
            mDb.setTransactionSuccessful();
            mState = STATE_SUCCESSFUL;
        }

        return this;
    }

    @NonNull
    public synchronized Transaction end() {
        return endTransaction();
    }

    @NonNull
    public synchronized Transaction endTransaction() {
        if (mState >= STATE_OPEN && mState < STATE_CLOSED) {
            mDb.endTransaction();
            mState = STATE_CLOSED;
        }

        return this;
    }
}
