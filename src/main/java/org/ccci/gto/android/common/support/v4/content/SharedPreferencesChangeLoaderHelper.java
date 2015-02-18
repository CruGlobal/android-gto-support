package org.ccci.gto.android.common.support.v4.content;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class SharedPreferencesChangeLoaderHelper {
    public static interface Interface {
        void addPreferenceKey(@Nullable String key);
    }

    private final Loader mLoader;
    private final SharedPreferences mPrefs;
    private final Set<String> mKeys = new HashSet<>();
    private final ChangeListener mChangeListener = new ChangeListener();

    SharedPreferencesChangeLoaderHelper(@NonNull final Loader loader, @NonNull final SharedPreferences prefs,
                                        @NonNull final String... keys) {
        mLoader = loader;
        mPrefs = prefs;
        mKeys.addAll(Arrays.asList(keys));
    }

    /* BEGIN lifecycle */

    void onStartLoading() {
        mPrefs.registerOnSharedPreferenceChangeListener(mChangeListener);
    }

    void onAbandon() {
        mPrefs.unregisterOnSharedPreferenceChangeListener(mChangeListener);
    }

    /* END lifecycle */

    void addPreferenceKey(@Nullable final String key) {
        mKeys.add(key);
    }

    private class ChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(@Nullable final SharedPreferences preferences,
                                              @Nullable final String key) {
            if (mKeys.contains(key)) {
                mLoader.onContentChanged();
            }
        }
    }
}
