package org.ccci.gto.android.common.eventbus.content;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.Loader;

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

    private boolean mRegistered = false;

    public EventBusLoaderHelper(@NonNull final Loader loader, @Nullable final EventBus eventBus) {
        mLoader = loader;

        if (eventBus != null) {
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

    /**
     * Callback to register the configured EventBus callbacks. This should be triggered before any other events within
     * {@link Loader#onStartLoading()}.
     */
    @MainThread
    public void onStartLoading() {
        synchronized (this) {
            // register all subscribers if they aren't already registered
            if (!mRegistered) {
                mRegistered = true;

                for (final EventBusSubscriber subscriber : mSubscribers) {
                    registerSubscriber(subscriber);
                }
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

        mRegistered = false;
    }

    private synchronized void registerSubscriber(EventBusSubscriber listener) {
        if (mRegistered) {
            mEventBusInstance.register(listener);
        }
    }

    private synchronized void unregisterSubscriber(EventBusSubscriber listener) {
        mEventBusInstance.unregister(listener);
    }
}
