package org.ccci.gto.android.common.util;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import java.util.Map;

public final class ThreadUtils {
    private ThreadUtils() {}

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

    @NonNull
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static <K> Object getLock(@NonNull final Map<K, Object> locks, @Nullable final K key) {
        synchronized (locks) {
            Object lock = locks.get(key);
            if (lock == null) {
                lock = new Object();
                locks.put(key, lock);
            }
            return lock;
        }
    }

    @NonNull
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static Object getLock(@NonNull final LongSparseArray<Object> locks, final long key) {
        synchronized (locks) {
            Object lock = locks.get(key);
            if (lock == null) {
                lock = new Object();
                locks.put(key, lock);
            }
            return lock;
        }
    }

    public static boolean isUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
