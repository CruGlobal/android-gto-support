package org.ccci.gto.android.common.eventbus.content;

import android.support.v4.content.Loader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class EventBusLoaderHelper {
    public interface Interface {
        void setEventBusListener(@NonNull Object listener);
    }

    @NonNull
    private final EventBus mEventBusInstance;
    @NonNull
    private final Loader mLoader;
    @NonNull
    private Object mEventBusListener;

    public EventBusLoaderHelper(@NonNull final Loader loader, @Nullable final Object listener,
                                @Nullable final EventBus eventBus) {
        mLoader = loader;
        setEventBusListener(listener);

        if(eventBus != null) {
            mEventBusInstance = eventBus;
        } else {
            mEventBusInstance = EventBus.getDefault();
        }
    }

    public void setEventBusListener(@Nullable Object listener) {
        if(listener == null) {
            listener = mLoader;
        }

        synchronized (this) {
            // Register the new listener and unregister the old listener.
            // We overlap registrations to not drop broadcasts
            if (mLoader.isStarted()) {
                registerListener(listener);
                unregisterListener(mEventBusListener);
            }

            // save new listener
            mEventBusListener = listener;
        }
    }

    public void onStartLoading() {
        synchronized (this) {
            registerListener(mEventBusListener);
        }
    }

    public void unregister() {
        synchronized (this) {
            unregisterListener(mEventBusListener);
        }
    }

    private synchronized void registerListener(Object listener) {
        mEventBusInstance.register(listener);
    }

    private synchronized void unregisterListener(Object listener) {
        if(mEventBusInstance.isRegistered(listener)) {
            mEventBusInstance.unregister(listener);
        }
    }
}
