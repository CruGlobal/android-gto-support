package org.ccci.gto.android.common.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import androidx.annotation.NonNull;

/**
 * @deprecated Since v3.3.0, we are favoring usage of Moshi in android apps over the usage of Gson.
 */
@Deprecated
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
