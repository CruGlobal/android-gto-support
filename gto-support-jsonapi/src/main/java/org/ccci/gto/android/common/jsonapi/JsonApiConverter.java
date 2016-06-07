package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.NonNull;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;

import java.util.HashMap;
import java.util.Map;

public class JsonApiConverter {
    private final Map<String, Class<?>> mTypes = new HashMap<>();

    public JsonApiConverter(@NonNull final Class<?>... classes) {
        for (final Class<?> c : classes) {
            // throw an exception if the provided class is not a valid JsonApiType
            final JsonApiType typeAnn = c.getAnnotation(JsonApiType.class);
            final String type = typeAnn != null ? typeAnn.value() : null;
            if (type == null) {
                throw new IllegalArgumentException("Class " + c + " is not a valid @JsonApiType");
            }

            // throw an exception if the specified type is already defined
            if (mTypes.containsKey(type)) {
                throw new IllegalArgumentException(
                        "Duplicate @JsonApiType(\"" + type + "\") shared by " + mTypes.get(type) + " and " + c);
            }

            // store this type
            mTypes.put(type, c);
        }
    }
}
