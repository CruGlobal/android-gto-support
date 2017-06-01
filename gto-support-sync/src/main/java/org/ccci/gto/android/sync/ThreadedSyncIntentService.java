package org.ccci.gto.android.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.WorkerThread;
import android.util.SparseBooleanArray;

import org.ccci.gto.android.common.app.ThreadedIntentService;

import java.util.concurrent.atomic.AtomicInteger;

import static org.ccci.gto.android.sync.ThreadedSyncIntentService.SyncTask.INITIAL_SYNC_ID;

public abstract class ThreadedSyncIntentService extends ThreadedIntentService {
    static final String EXTRA_SYNCID = ThreadedSyncIntentService.class.getName() + ".EXTRA_SYNCID";

    static final SparseBooleanArray SYNCS_RUNNING = new SparseBooleanArray();

    protected ThreadedSyncIntentService(@NonNull final String name) {
        super(name);
    }

    protected ThreadedSyncIntentService(@NonNull final String name, final int poolSize) {
        super(name, poolSize);
    }

    /* BEGIN lifecycle */

    @Override
    protected final void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null) {
            try {
                onHandleSyncIntent(intent);
            } finally {
                final int syncId = intent.getIntExtra(EXTRA_SYNCID, 0);
                synchronized (SYNCS_RUNNING) {
                    SYNCS_RUNNING.delete(syncId);
                }
                finishSync(syncId);
            }
        }
    }

    @WorkerThread
    protected abstract void onHandleSyncIntent(@NonNull Intent intent);

    /* END lifecycle */

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected void finishSync(final int syncId) {}

    public static boolean isSyncRunning(final int syncId) {
        if (syncId < INITIAL_SYNC_ID) {
            return false;
        }

        synchronized (SYNCS_RUNNING) {
            return SYNCS_RUNNING.get(syncId, false);
        }
    }

    public static final class SyncTask implements Runnable {
        static final int INITIAL_SYNC_ID = 1;
        private static final AtomicInteger NEXT_SYNC_ID = new AtomicInteger(INITIAL_SYNC_ID);

        @NonNull
        private final Context mContext;
        @NonNull
        private final Intent mTask;

        public SyncTask(@NonNull final Context context, @NonNull final Intent task) {
            mContext = context.getApplicationContext();
            mTask = task;
        }

        @Override
        public void run() {
            sync();
        }

        public int sync() {
            final int syncId = NEXT_SYNC_ID.getAndIncrement();
            mTask.putExtra(EXTRA_SYNCID, syncId);
            synchronized (SYNCS_RUNNING) {
                SYNCS_RUNNING.put(syncId, true);
            }
            mContext.startService(mTask);
            return syncId;
        }
    }
}
