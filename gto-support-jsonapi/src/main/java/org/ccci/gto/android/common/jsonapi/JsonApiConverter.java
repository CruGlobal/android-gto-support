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

    @NonNull
    public <T> JsonApiObject<T> fromJson(@NonNull final String json, @NonNull final Class<T> clazz)
            throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        final JsonApiObject<T> output;
        if (jsonObject.has(JSON_DATA)) {
            // {data: []}
            if (jsonObject.optJSONArray(JSON_DATA) != null) {
                final JSONArray data = jsonObject.optJSONArray(JSON_DATA);
                output = JsonApiObject.of();
                for (int i = 0; i < data.length(); i++) {
                    final Object resource = fromJson(data.optJSONObject(i));
                    if (clazz.isInstance(resource)) {
                        output.addData(clazz.cast(resource));
                    }
                }
            }
            // {data: null} or {data: {}}
            else {
                output = JsonApiObject.single(null);
                final Object resource = fromJson(jsonObject.optJSONObject(JSON_DATA));
                if (clazz.isInstance(resource)) {
                    output.setData(clazz.cast(resource));
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return output;
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
    private Object fromJson(@Nullable final JSONObject json) {
        if (json == null) {
            return null;
        }

        // determine the type
        final Class<?> type = mTypes.get(json.optString(JSON_DATA_TYPE));
        if (type == null) {
            return null;
        }

        // create an instance of this type
        final Object instance;
        try {
            instance = type.newInstance();
        } catch (final Exception e) {
            return null;
        }

        // populate fields
        final JSONObject attributes = json.optJSONObject(JSON_DATA_ATTRIBUTES);
        for (final Field field : mFields.get(type)) {
            try {
                // handle id fields
                if (field.getAnnotation(JsonApiId.class) != null) {
                    setField(instance, field, json, JSON_DATA_ID);
                }
                // anything else is an attribute
                else {
                    final JsonApiAttribute attr = field.getAnnotation(JsonApiAttribute.class);
                    final String name = attr != null && attr.name().length() > 0 ? attr.name() : field.getName();
                    setField(instance, field, attributes, name);
                }
            } catch (final JSONException | IllegalAccessException ignored) {
            }
        }

        // return the object
        return instance;
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

    private void setField(@NonNull final Object obj, @NonNull final Field field, @NonNull final JSONObject json,
                          @NonNull final String name) throws JSONException, IllegalAccessException {
        final Class<?> fieldType = field.getType();
        if (fieldType.isAssignableFrom(String.class)) {
            field.set(obj, json.getString(name));
        } else if (fieldType.isAssignableFrom(Double.class)) {
            field.set(obj, json.getDouble(name));
        } else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
            field.set(obj, json.getInt(name));
        } else if (fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)) {
            field.set(obj, json.getLong(name));
        } else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
            field.set(obj, json.getBoolean(name));
        }
    }
}
