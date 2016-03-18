package org.ccci.gto.android.common.eventbus.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public final class EventBusLoaderHelper {
    public interface Interface {
        void addEventBusSubscriber(@NonNull EventBusSubscriber subscriber);

        void removeEventBusSubscriber(@NonNull EventBusSubscriber subscriber);
    }

    @NonNull
    private final EventBus mEventBusInstance;
    @NonNull
    private final Loader mLoader;
    @NonNull
    private final List<EventBusSubscriber> mSubscribers = new ArrayList<>();

    public EventBusLoaderHelper(@NonNull final Loader loader, @Nullable final EventBus eventBus) {
        mLoader = loader;

        if(eventBus != null) {
            mEventBusInstance = eventBus;
        } else {
            mEventBusInstance = EventBus.getDefault();
        }
    }

    public synchronized void addEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mSubscribers.add(subscriber);
        registerSubscriber(subscriber);
    }

    public synchronized void removeEventBusSubscriber(@NonNull final EventBusSubscriber subscriber) {
        mSubscribers.remove(subscriber);
        unregisterSubscriber(subscriber);
    }

    public void onStartLoading() {
        synchronized (this) {
            for (final EventBusSubscriber subscriber : mSubscribers) {
                registerSubscriber(subscriber);
            }
        }
    }

    public void onAbandon() {
        unregisterSubscribers();
    }

    public void onReset() {
        unregisterSubscribers();
    }

    private synchronized void unregisterSubscribers() {
        for (final EventBusSubscriber subscriber : mSubscribers) {
            unregisterSubscriber(subscriber);
        }
    }

    private synchronized void registerSubscriber(EventBusSubscriber listener) {
        if (mLoader.isStarted() && !mLoader.isAbandoned()) {
            mEventBusInstance.register(listener);
        }
    }

    private synchronized void unregisterSubscriber(EventBusSubscriber listener) {
        if(mEventBusInstance.isRegistered(listener)) {
            mEventBusInstance.unregister(listener);
        }
    }
}
