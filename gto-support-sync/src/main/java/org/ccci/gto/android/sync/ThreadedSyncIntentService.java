package org.ccci.gto.android.sync;

import android.content.Context;
import android.content.Intent;

import org.ccci.gto.android.common.app.ThreadedIntentService;
import org.ccci.gto.android.common.sync.SyncRegistry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

public abstract class ThreadedSyncIntentService extends ThreadedIntentService {
    static final String EXTRA_SYNCID = ThreadedSyncIntentService.class.getName() + ".EXTRA_SYNCID";

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
                SyncRegistry.INSTANCE.finishSync(syncId);
                finishSync(syncId);
            }
        }
    }

    @WorkerThread
    protected abstract void onHandleSyncIntent(@NonNull Intent intent);

    /* END lifecycle */

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected void finishSync(final int syncId) {}

    /**
     * @deprecated Since v3.5.0, use the SyncRegistry to determine if a sync is running.
     */
    @Deprecated
    public static boolean isSyncRunning(final int syncId) {
        return SyncRegistry.INSTANCE.isSyncRunning(syncId);
    }

    public static final class SyncTask implements Runnable {
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
            final int syncId = SyncRegistry.INSTANCE.startSync();
            mTask.putExtra(EXTRA_SYNCID, syncId);
            mContext.startService(mTask);
            return syncId;
        }
    }
}
