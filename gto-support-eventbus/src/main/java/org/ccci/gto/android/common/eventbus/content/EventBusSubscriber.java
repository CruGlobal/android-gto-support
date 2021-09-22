package org.ccci.gto.android.common.eventbus.content;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
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
