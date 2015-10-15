package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public final class GenericKey {
    @NonNull
    private final Object[] mKey;

    public GenericKey(@NonNull final Object... key) {
        mKey = key;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GenericKey that = (GenericKey) o;
        return Arrays.deepEquals(mKey, that.mKey);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mKey);
    }
}
