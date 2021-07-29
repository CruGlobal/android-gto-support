package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

/**
 * @deprecated Since v3.9.0, use LiveData & coroutines to asynchronously load data for a UI.
 */
@Deprecated
public abstract class CachingAsyncTaskLoader<D> extends AsyncTaskLoader<D> {
    @Nullable
    private D mData;

    public CachingAsyncTaskLoader(@NonNull final Context context) {
        super(context);
    }

    /* BEGIN lifecycle */

    @Override
    @MainThread
    protected void onStartLoading() {
        super.onStartLoading();

        // deliver already loaded data
        if (mData != null) {
            deliverResult(mData);
        }

        // force a fresh load if needed
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    @MainThread
    protected void onReset() {
        super.onReset();
        mData = null;
    }

    /* END lifecycle */

    @Override
    @MainThread
    public void deliverResult(@Nullable final D data) {
        // An async query came in while the loader is reset
        if (isReset()) {
            return;
        }

        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
