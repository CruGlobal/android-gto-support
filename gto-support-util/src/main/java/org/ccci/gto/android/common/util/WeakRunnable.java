package org.ccci.gto.android.common.util;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public final class WeakRunnable implements Runnable {
    @NonNull
    private final WeakReference<Runnable> mRunnable;

    public WeakRunnable(@NonNull final Runnable runnable) {
        mRunnable = new WeakReference<>(runnable);
    }

    @Override
    public void run() {
        final Runnable runnable = mRunnable.get();
        if (runnable != null) {
            runnable.run();
        }
    }
}
