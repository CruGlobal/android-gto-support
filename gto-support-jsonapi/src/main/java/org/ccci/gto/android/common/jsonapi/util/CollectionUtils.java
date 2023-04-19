package org.ccci.gto.android.common.jsonapi.util;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class CollectionUtils {
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Collection> T newCollection(@Nullable final Class<T> type) {
        if (type == null) {
            return null;
        }

        // try creating the collection via reflection, suppress exceptions to allow generic types to be used.
        try {
            return type.newInstance();
        } catch (final Throwable ignored) {
        }

        // try using some generic collection type
        if (type.isAssignableFrom(ArrayList.class)) {
            return (T) new ArrayList();
        } else if (type.isAssignableFrom(HashSet.class)) {
            return (T) new HashSet();
        } else {
            throw new IllegalArgumentException(
                    type + " is currently not a supported Collection type, try something more generic");
        }
    }
}
