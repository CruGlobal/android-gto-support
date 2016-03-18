package org.ccci.gto.android.common.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

/**
 * @deprecated this should be handled by manually including the LoaderHelpers you want to utilize in your own Loader
 */
@Deprecated
public abstract class AsyncTaskBroadcastReceiverSharedPreferencesChangeLoader<D> extends AsyncTaskLoader<D>
        implements BroadcastReceiverLoaderHelper.Interface, SharedPreferencesChangeLoaderHelper.Interface {
    private final BroadcastReceiverLoaderHelper mHelper1;
    private final SharedPreferencesChangeLoaderHelper mHelper2;

    @NonNull
    protected final SharedPreferences mPrefs;

    @Nullable
    private D mData;

    public AsyncTaskBroadcastReceiverSharedPreferencesChangeLoader(@NonNull final Context context,
                                                                   @NonNull final SharedPreferences prefs) {
        super(context);
        mHelper1 = new BroadcastReceiverLoaderHelper(this);
        mHelper2 = new SharedPreferencesChangeLoaderHelper(this, prefs);
        mPrefs = prefs;
    }

    /* BEGIN lifecycle */

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mHelper1.onStartLoading();
        mHelper2.onStartLoading();

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
        mHelper1.onAbandon();
        mHelper2.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();
        mHelper1.onReset();
        mHelper2.onReset();
    }

    /* END lifecycle */

    @Override
    public final void addIntentFilter(@NonNull final IntentFilter filter) {
        mHelper1.addIntentFilter(filter);
    }

    @Override
    public final void addPreferenceKey(@Nullable final String key) {
        mHelper2.addPreferenceKey(key);
    }

    @Override
    public final void removePreferenceKey(@Nullable final String key) {
        mHelper2.removePreferenceKey(key);
    }

    @Override
    public final void setBroadcastReceiver(@Nullable final BroadcastReceiver receiver) {
        mHelper1.setBroadcastReceiver(receiver);
    }
}
