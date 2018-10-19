package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public final class WeakTask<T> implements Runnable {
    private final Reference<T> mRef;
    private final Task<T> mTask;

    public WeakTask(@NonNull final T obj, @NonNull final Task<T> task) {
        mRef = new WeakReference<>(obj);
        mTask = task;
    }

    @Override
    public void run() {
        final T obj = mRef.get();
        if (obj != null) {
            mTask.run(obj);
        }
    }

    @FunctionalInterface
    public interface Task<T> {
        void run(@NonNull T obj);
    }
}
