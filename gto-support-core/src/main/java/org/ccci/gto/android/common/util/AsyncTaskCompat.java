package org.ccci.gto.android.common.util;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;

/**
 * @deprecated Since v3.0.0, use {@link AsyncTask} directly.
 */
@Deprecated
public class AsyncTaskCompat {
    public static final Executor SERIAL_EXECUTOR = AsyncTask.SERIAL_EXECUTOR;

    public static void execute(@NonNull final Runnable task) {
        AsyncTask.execute(task);
    }
}
