package org.ccci.gto.android.common.eventbus.task;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

public final class EventBusDelayedPost implements Runnable {
    @NonNull
    private final EventBus mEventBus;
    @NonNull
    private final Object[] mEvents;

    public EventBusDelayedPost(@NonNull final EventBus eventBus, @NonNull final Object... events) {
        mEventBus = eventBus;
        mEvents = events;
    }

    @Override
    public void run() {
        for (final Object event : mEvents) {
            if (event != null) {
                mEventBus.post(event);
            }
        }
    }
}
