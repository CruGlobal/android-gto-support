package org.ccci.gto.android.common.support.v4.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public abstract class SimpleLoaderCallbacks<D> implements LoaderManager.LoaderCallbacks<D> {
    @Override
    public void onLoaderReset(@NonNull final Loader<D> loader) {
        // reset the data
        this.onLoadFinished(loader, null);
    }

    @Nullable
    @Override
    public abstract Loader<D> onCreateLoader(int id, @Nullable Bundle args);

    @Override
    public abstract void onLoadFinished(@NonNull Loader<D> loader, @Nullable D data);
}
