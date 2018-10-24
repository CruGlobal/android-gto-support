package org.ccci.gto.android.common.api;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Object representing an individual session for this API. Can be extended to track additional session data.
 */
public class Session {
    protected static final String PREF_SESSION_BASE_NAME = "session";

    @NonNull
    private final String mBaseAttrName;

    @Nullable
    public final String id;

    public Session(@Nullable final String id) {
        this(id, PREF_SESSION_BASE_NAME);
    }

    public Session(@Nullable final String id, @Nullable final String baseAttrName) {
        mBaseAttrName = baseAttrName != null ? baseAttrName : PREF_SESSION_BASE_NAME;
        this.id = id;
    }

    public Session(@NonNull final SharedPreferences prefs) {
        this(prefs, null);
    }

    public Session(@NonNull final SharedPreferences prefs, @Nullable final String baseAttrName) {
        mBaseAttrName = baseAttrName != null ? baseAttrName : PREF_SESSION_BASE_NAME;
        this.id = prefs.getString(getPrefAttrName("id"), null);
    }

    public boolean isValid() {
        return this.id != null;
    }

    @NonNull
    public final String getPrefAttrName(@NonNull final String type) {
        return mBaseAttrName + "." + type;
    }

    public void save(@NonNull final SharedPreferences.Editor prefs) {
        prefs.putString(getPrefAttrName("id"), this.id);
    }

    public void delete(@NonNull final SharedPreferences.Editor prefs) {
        prefs.remove(getPrefAttrName("id"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Session that = (Session) o;
        return TextUtils.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {id});
    }
}
