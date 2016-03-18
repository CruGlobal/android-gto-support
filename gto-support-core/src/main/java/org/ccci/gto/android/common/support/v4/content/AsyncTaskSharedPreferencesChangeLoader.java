package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AsyncTaskSharedPreferencesChangeLoader<D> extends AsyncTaskLoader<D>
        implements SharedPreferencesChangeLoaderHelper.Interface {
    private final SharedPreferencesChangeLoaderHelper mHelper;

    @NonNull
    protected final SharedPreferences mPrefs;

    private D mData;

    public AsyncTaskSharedPreferencesChangeLoader(@NonNull final Context context, @NonNull final String prefsName) {
        this(context, context.getSharedPreferences(prefsName, Context.MODE_PRIVATE));
    }

    public AsyncTaskSharedPreferencesChangeLoader(@NonNull final Context context,
                                                  @NonNull final SharedPreferences prefs) {
        super(context);
        mPrefs = prefs;
        mHelper = new SharedPreferencesChangeLoaderHelper(this, mPrefs);
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mHelper.onStartLoading();

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
    protected void onAbandon() {
        super.onAbandon();
        mHelper.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();
        mHelper.onReset();
    }

    /* END lifecycle */

    @Override
    public final void addPreferenceKey(@Nullable final String key) {
        mHelper.addPreferenceKey(key);
    }

    @Override
    public void removePreferenceKey(@Nullable final String key) {
        mHelper.removePreferenceKey(key);
    }
}
