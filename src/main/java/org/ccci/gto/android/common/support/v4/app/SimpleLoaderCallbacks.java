package org.ccci.gto.android.common.support.v4.app;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public abstract class SimpleLoaderCallbacks<D> implements LoaderManager.LoaderCallbacks<D> {
    @Override
    public void onLoaderReset(final Loader<D> loader) {
        // reset the data
        this.onLoadFinished(loader, null);
    }
}