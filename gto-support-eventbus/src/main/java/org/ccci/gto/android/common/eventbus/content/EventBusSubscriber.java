package org.ccci.gto.android.common.eventbus.content;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

public abstract class EventBusSubscriber {
    @NonNull
    private final Loader mLoader;

    public EventBusSubscriber(@NonNull final Loader loader) {
        mLoader = loader;
    }

    @MainThread
    protected final void triggerLoad() {
        // don't trigger a load when the loader has been abandoned
        if (!mLoader.isAbandoned()) {
            mLoader.onContentChanged();
        }
    }
}
