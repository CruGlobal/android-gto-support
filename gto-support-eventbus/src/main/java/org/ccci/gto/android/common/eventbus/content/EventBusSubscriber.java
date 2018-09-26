package org.ccci.gto.android.common.eventbus.content;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

public abstract class EventBusSubscriber {
    @NonNull
    private final Loader mLoader;

    public EventBusSubscriber(@NonNull final Loader loader) {
        mLoader = loader;
    }

    @MainThread
    protected final void triggerLoad() {
        mLoader.onContentChanged();
    }
}
