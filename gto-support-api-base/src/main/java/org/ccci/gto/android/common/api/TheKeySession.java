package org.ccci.gto.android.common.api;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TheKeySession extends Session {
    @Nullable
    private final String mGuid;

    public TheKeySession(@Nullable final String id, @Nullable final String guid) {
        this(id, guid, PREF_SESSION_BASE_NAME);
    }

    public TheKeySession(@Nullable final String id, @Nullable final String guid, @NonNull final String baseAttrName) {
        super(id, (guid != null ? guid.toUpperCase(Locale.US) + "." : "") + baseAttrName);
        mGuid = guid != null ? guid.toUpperCase(Locale.US) : null;
    }

    public TheKeySession(@NonNull final SharedPreferences prefs, @Nullable final String guid) {
        this(prefs, guid, PREF_SESSION_BASE_NAME);
    }

    public TheKeySession(@NonNull final SharedPreferences prefs, @Nullable final String guid,
                         @NonNull final String baseAttrName) {
        super(prefs, (guid != null ? guid.toUpperCase(Locale.US) + "." : "") + baseAttrName);
        mGuid = guid != null ? guid.toUpperCase(Locale.US) : null;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && mGuid != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TheKeySession that = (TheKeySession) o;
        return super.equals(o) && TextUtils.equals(mGuid, that.mGuid);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + Arrays.hashCode(new Object[] {mGuid});
    }
}
