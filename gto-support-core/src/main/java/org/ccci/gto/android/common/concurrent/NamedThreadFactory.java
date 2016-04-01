package org.ccci.gto.android.common.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    @NonNull
    private final String mName;
    private final AtomicInteger mCount = new AtomicInteger(1);

    public NamedThreadFactory(@NonNull final String name) {
        mName = name;
    }

    @Override
    public Thread newThread(final Runnable r) {
        return new Thread(r, mName + " #" + mCount.getAndIncrement());
    }
}
