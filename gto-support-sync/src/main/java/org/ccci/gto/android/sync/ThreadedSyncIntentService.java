package org.ccci.gto.android.sync;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

import org.ccci.gto.android.common.app.ThreadedIntentService;
import org.ccci.gto.android.common.sync.SyncRegistry;

/**
 * @deprecated Since v3.10.0, Use WorkManager or JobIntentService to manage background sync work instead.
 */
@Deprecated
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

    public static final class SyncTask implements Runnable, org.ccci.gto.android.common.sync.SyncTask {
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

        @Override
        public int sync() {
            final int syncId = SyncRegistry.INSTANCE.startSync();
            mTask.putExtra(EXTRA_SYNCID, syncId);
            mContext.startService(mTask);
            return syncId;
        }
    }
}
