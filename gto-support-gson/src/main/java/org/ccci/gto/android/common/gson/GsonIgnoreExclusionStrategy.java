package org.ccci.gto.android.common.gson;

import android.support.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonIgnoreExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(@NonNull final FieldAttributes f) {
        return f.getAnnotation(GsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(@NonNull final Class<?> clazz) {
        return clazz.getAnnotation(GsonIgnore.class) != null;
    }
}
