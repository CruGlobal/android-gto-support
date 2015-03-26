package org.ccci.gto.android.common.support.v4.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

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