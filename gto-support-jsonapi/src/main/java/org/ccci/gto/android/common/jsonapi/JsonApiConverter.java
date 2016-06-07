package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ATTRIBUTES;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ID;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_TYPE;

public class JsonApiConverter {
    private final Map<String, Class<?>> mTypes = new HashMap<>();
    private final Map<Class<?>, List<Field>> mFields = new HashMap<>();

    public JsonApiConverter(@NonNull final Class<?>... classes) {
        for (final Class<?> c : classes) {
            // throw an exception if the provided class is not a valid JsonApiType
            final String type = getType(c);
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
            mFields.put(c, getFields(c));
        }
    }

    @NonNull
    public String toJson(@NonNull final JsonApiObject<?> obj) {
        try {
            final JSONObject json = new JSONObject();
            if (obj.isSingle()) {
                final Object resource = obj.getDataSingle();
                if (resource == null) {
                    json.put(JSON_DATA, JSONObject.NULL);
                } else {
                    json.put(JSON_DATA, toJson(resource));
                }
            } else {
                final JSONArray dataArr = new JSONArray();
                for (final Object resource : obj.getData()) {
                    dataArr.put(toJson(resource));
                }
                json.put(JSON_DATA, dataArr);
            }

            return json.toString();
        } catch (final JSONException e) {
            throw new RuntimeException("Unexpected JSONException", e);
        }
    }

    @Nullable
    @SuppressWarnings("checkstyle:RightCurly")
    private JSONObject toJson(@Nullable final Object resource) throws JSONException {
        if (resource == null) {
            return null;
        }
        final Class<?> clazz = resource.getClass();
        final String type = getType(clazz);
        if (!clazz.equals(mTypes.get(type))) {
            throw new IllegalArgumentException(clazz + " is not a valid JsonApi resource type for this converter");
        }

        // create base object
        final JSONObject json = new JSONObject();
        final JSONObject attributes = new JSONObject();
        json.put(JSON_DATA_TYPE, type);
        json.put(JSON_DATA_ATTRIBUTES, attributes);

        // attach all fields
        for (final Field field : mFields.get(clazz)) {
            final JsonApiAttribute attr = field.getAnnotation(JsonApiAttribute.class);

            Object value;
            try {
                value = field.get(resource);
            } catch (final IllegalAccessException e) {
                value = null;
            }

            // skip null values
            if (value == null) {
                continue;
            }

            // handle id fields
            if (field.getAnnotation(JsonApiId.class) != null) {
                json.put(JSON_DATA_ID, value);
            }
            // everything else is a regular attribute
            else {
                final String name = attr != null && attr.name().length() > 0 ? attr.name() : field.getName();
                attributes.put(name, value);
            }
        }

        return json;
    }

    @Nullable
    private String getType(@NonNull final Class<?> clazz) {
        final JsonApiType type = clazz.getAnnotation(JsonApiType.class);
        return type != null ? type.value() : null;
    }

    @NonNull
    private List<Field> getFields(@Nullable final Class<?> type) {
        final List<Field> fields = new ArrayList<>();

        if (type != null && !Object.class.equals(type)) {
            for (final Field field : type.getDeclaredFields()) {
                final int modifiers = field.getModifiers();

                // skip static and transient fields
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                if (field.getAnnotation(JsonApiIgnore.class) != null) {
                    continue;
                }

                // set field as accessible and track it
                field.setAccessible(true);
                fields.add(field);
            }

            // process the superclass
            fields.addAll(getFields(type.getSuperclass()));
        }

        return fields;
    }
}
