package org.ccci.gto.android.common.util;

import android.os.Looper;

import java.util.Map;

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
}
