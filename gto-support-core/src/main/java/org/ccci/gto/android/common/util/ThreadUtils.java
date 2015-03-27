package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadUtils {
    private ThreadUtils() {
    }

    public static void assertNotOnUiThread() {
        if (isUiThread()) {
            throw new RuntimeException("unsupported method on UI thread");
        }
    }

    public static void assertOnUiThread() {
        if (!isUiThread()) {
            throw new RuntimeException("method requires UI thread");
        }
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static <K> Object getLock(final Map<K, Object> locks, final K key) {
        synchronized (locks) {
            if (!locks.containsKey(key)) {
                locks.put(key, new Object());
            }
            return locks.get(key);
        }
    }

    public static boolean isUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void runOnBackgroundThread(@NonNull final Runnable task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            AsyncTask.execute(task);
        } else {
            getExecutor().execute(task);
        }
    }

    private static final Object LOCK_EXECUTOR = new Object();
    private static Executor EXECUTOR;

    @NonNull
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Executor getExecutor() {
        synchronized (LOCK_EXECUTOR) {
            if (EXECUTOR == null) {
                EXECUTOR = Executors.newFixedThreadPool(1);
                if (EXECUTOR instanceof ThreadPoolExecutor) {
                    ((ThreadPoolExecutor) EXECUTOR).setKeepAliveTime(30, TimeUnit.SECONDS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        ((ThreadPoolExecutor) EXECUTOR).allowCoreThreadTimeOut(true);
                    } else {
                        ((ThreadPoolExecutor) EXECUTOR).setCorePoolSize(0);
                    }
                }
            }

            return EXECUTOR;
        }
    }
}
