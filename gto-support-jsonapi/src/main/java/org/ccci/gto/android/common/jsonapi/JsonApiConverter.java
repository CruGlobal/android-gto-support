package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.converter.TypeConverter;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ATTRIBUTES;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ID;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_RELATIONSHIPS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_TYPE;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_INCLUDED;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_META;
import static org.ccci.gto.android.common.util.CollectionUtils.newCollection;

public final class JsonApiConverter {
    public static final class Builder {
        private final List<Class<?>> mClasses = new ArrayList<>();
        private final List<TypeConverter<?>> mConverters = new ArrayList<>();

        @NonNull
        public Builder addClasses(@NonNull final Class<?>... classes) {
            mClasses.addAll(Arrays.asList(classes));
            return this;
        }

        @NonNull
        public Builder addConverters(@NonNull final TypeConverter<?>... converters) {
            mConverters.addAll(Arrays.asList(converters));
            return this;
        }

        @NonNull
        public JsonApiConverter build() {
            return new JsonApiConverter(mClasses, mConverters);
        }
    }

    private final List<TypeConverter<?>> mConverters = new ArrayList<>();
    private final Set<Class<?>> mSupportedClasses = new HashSet<>();
    private final Map<String, Class<?>> mTypes = new HashMap<>();
    private final Map<Class<?>, List<Field>> mFields = new HashMap<>();

    private JsonApiConverter(@NonNull final List<Class<?>> classes, @NonNull final List<TypeConverter<?>> converters) {
        mConverters.addAll(converters);
        mSupportedClasses.addAll(classes);

        for (final Class<?> c : classes) {
            // throw an exception if the provided class is not a valid JsonApiType
            final String type = getResourceType(c);
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

            // handle any type aliases
            for (final String alias : getResourceTypeAliases(c)) {
                // throw an exception if the specified alias is already defined
                if (mTypes.containsKey(alias)) {
                    throw new IllegalArgumentException(
                            "Duplicate @JsonApiType(\"" + alias + "\") shared by " + mTypes.get(alias) + " and " + c);
                }

                mTypes.put(alias, c);
            }
        }
    }

    public boolean supports(@Nullable final Class<?> c) {
        return mSupportedClasses.contains(c);
    }

    @NonNull
    public String toJson(@NonNull final JsonApiObject<?> obj) {
        try {
            final JSONObject json = new JSONObject();
            final Map<ObjKey, JSONObject> related = new HashMap<>();
            if (obj.isSingle()) {
                final Object resource = obj.getDataSingle();
                if (resource == null) {
                    json.put(JSON_DATA, JSONObject.NULL);
                } else {
                    json.put(JSON_DATA, resourceToJson(resource, related));
                }
            } else {
                final JSONArray dataArr = new JSONArray();
                for (final Object resource : obj.getData()) {
                    dataArr.put(resourceToJson(resource, related));
                }
                json.put(JSON_DATA, dataArr);
            }

            // include related objects if there are any
            if (related.size() > 0) {
                json.put(JSON_INCLUDED, new JSONArray(related.values()));
            }

            // pass the JSONApi meta data as-is
            json.put(JSON_META, obj.getRawMeta());

            return json.toString();
        } catch (final JSONException e) {
            throw new RuntimeException("Unexpected JSONException", e);
        }
    }

    @NonNull
    @SuppressWarnings("checkstyle:RightCurly")
    public <T> JsonApiObject<T> fromJson(@NonNull final String json, @NonNull final Class<T> type)
            throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);

        // parse "included" objects
        final Map<ObjKey, Object> objects = new HashMap<>();
        final JSONArray included = jsonObject.optJSONArray(JSON_INCLUDED);
        if (included != null) {
            //noinspection unchecked
            resourcesFromJson(included, Object.class, Collection.class, objects);
        }

        final JsonApiObject<T> output;
        if (jsonObject.has(JSON_DATA)) {
            // {data: []}
            final JSONArray dataArray = jsonObject.optJSONArray(JSON_DATA);
            if (dataArray != null) {
                output = JsonApiObject.of();
                //noinspection unchecked
                output.setData(resourcesFromJson(dataArray, type, Collection.class, objects));
            }
            // {data: null} or {data: {}}
            else {
                output = JsonApiObject.single(null);
                final T resource = resourceFromJson(jsonObject.optJSONObject(JSON_DATA), type, objects);
                if (resource != null) {
                    output.setData(resource);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }

        // pass the JSONApi meta object back as-is
        output.setRawMeta(jsonObject.optJSONObject(JSON_META));

        return output;
    }

    @Nullable
    @SuppressWarnings("checkstyle:RightCurly")
    private JSONObject resourceToJson(@Nullable final Object resource, @NonNull final Map<ObjKey, JSONObject> related)
            throws JSONException {
        if (resource == null) {
            return null;
        }
        final Class<?> clazz = resource.getClass();
        final String type = getResourceType(clazz);
        if (!clazz.equals(mTypes.get(type))) {
            throw new IllegalArgumentException(clazz + " is not a valid JsonApi resource type for this converter");
        }

        // create base object
        final JSONObject json = new JSONObject();
        final JSONObject attributes = new JSONObject();
        final JSONObject relationships = new JSONObject();
        json.put(JSON_DATA_TYPE, type);

        // process all fields
        for (final Field field : mFields.get(clazz)) {
            final Class<?> fieldType = field.getType();
            final Class<?> fieldCollectionType = getFieldCollectionType(field.getGenericType());

            // is this a relationship?
            if (supports(fieldType)) {
                try {
                    final JSONObject relatedObj = resourceToJson(field.get(resource), related);
                    final ObjKey key = ObjKey.create(relatedObj);
                    if (key != null) {
                        related.put(key, relatedObj);
                        final JSONObject reference =
                                new JSONObject(relatedObj, new String[] {JSON_DATA_TYPE, JSON_DATA_ID});
                        relationships.put(getFieldName(field), new JSONObject(singletonMap(JSON_DATA, reference)));
                    }
                } catch (final IllegalAccessException ignored) {
                }
                continue;
            } else if (supports(fieldCollectionType)) {
                final JSONArray objs = new JSONArray();
                try {
                    final Collection col = (Collection) field.get(resource);
                    if (col != null) {
                        for (final Object obj : col) {
                            final JSONObject relatedObj = resourceToJson(obj, related);
                            final ObjKey key = ObjKey.create(relatedObj);
                            if (key != null) {
                                related.put(key, relatedObj);
                                objs.put(new JSONObject(relatedObj, new String[] {JSON_DATA_TYPE, JSON_DATA_ID}));
                            }
                        }
                    }
                } catch (final IllegalAccessException ignored) {
                }
                relationships.put(getFieldName(field), new JSONObject(singletonMap(JSON_DATA, objs)));
                continue;
            }

            Object value;
            try {
                value = convertToJsonValue(field.get(resource));
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
                attributes.put(getFieldName(field), value);
            }
        }

        // attach attributes
        if (attributes.length() > 0) {
            json.put(JSON_DATA_ATTRIBUTES, attributes);
        }
        // attach relationships
        if (relationships.length() > 0) {
            json.put(JSON_DATA_RELATIONSHIPS, relationships);
        }

        return json;
    }

    @NonNull
    private <E, T extends Collection<E>> T resourcesFromJson(@Nullable final JSONArray json,
                                                             @NonNull final Class<E> type,
                                                             @NonNull final Class<T> collectionType,
                                                             @NonNull final Map<ObjKey, Object> objects) {
        // create new collection
        final T resources = newCollection(collectionType);
        if (resources == null) {
            throw new IllegalArgumentException("Invalid Collection Type: " + collectionType);
        }

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                final E resource = resourceFromJson(json.optJSONObject(i), type, objects);
                if (resource != null) {
                    resources.add(resource);
                }
            }
        }

        return resources;
    }

    @Nullable
    @SuppressWarnings("checkstyle:RightCurly")
    private <E> E resourceFromJson(@Nullable final JSONObject json, @NonNull final Class<E> expectedType,
                                   @NonNull final Map<ObjKey, Object> objects) {
        if (json == null) {
            return null;
        }

        // determine the type
        final String rawType = json.optString(JSON_DATA_TYPE);
        final Class<?> type = mTypes.get(rawType);
        if (type == null || !expectedType.isAssignableFrom(type)) {
            return null;
        }

        // look for the referenced object first
        final String rawId = json.optString(JSON_DATA_ID);
        final ObjKey key = rawId != null && rawType != null ? new ObjKey(rawType, rawId) : null;
        E instance = null;
        if (key != null) {
            //noinspection unchecked
            instance = (E) objects.get(key);
        }
        // no object found, create a new instance
        if (instance == null) {
            try {
                //noinspection unchecked
                instance = (E) type.newInstance();
                if (key != null) {
                    objects.put(key, instance);
                }
            } catch (final Exception e) {
                return null;
            }
        }

        // populate fields
        final JSONObject attributes = json.optJSONObject(JSON_DATA_ATTRIBUTES);
        final JSONObject relationships = json.optJSONObject(JSON_DATA_RELATIONSHIPS);
        for (final Field field : mFields.get(type)) {
            final Class<?> fieldType = field.getType();
            final Class<?> fieldCollectionType = getFieldCollectionType(field.getGenericType());

            try {
                // handle id fields
                if (field.getAnnotation(JsonApiId.class) != null) {
                    field.set(instance, convertFromJSONObject(json, JSON_DATA_ID, fieldType));
                }
                // handle relationships
                else if (supports(fieldType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(getFieldName(field));
                        if (related != null) {
                            field.set(instance, resourceFromJson(related.optJSONObject(JSON_DATA), fieldType, objects));
                        }
                    }
                }
                // handle collections of relationships
                else if (supports(fieldCollectionType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(getFieldName(field));
                        if (related != null) {
                            field.set(instance, resourcesFromJson(related.optJSONArray(JSON_DATA), fieldCollectionType,
                                                                  (Class<? extends Collection>) fieldType, objects));
                        }
                    }
                }
                // anything else is an attribute
                else {
                    if (attributes != null) {
                        field.set(instance, convertFromJSONObject(attributes, getFieldName(field), fieldType));
                    }
                }
            } catch (final JSONException | IllegalAccessException ignored) {
            }
        }

        // return the object
        return instance;
    }

    @Nullable
    private String getResourceType(@NonNull final Class<?> clazz) {
        final JsonApiType type = clazz.getAnnotation(JsonApiType.class);
        return type != null ? type.value() : null;
    }

    @NonNull
    private String[] getResourceTypeAliases(@NonNull final Class<?> clazz) {
        final JsonApiType type = clazz.getAnnotation(JsonApiType.class);
        return type != null ? type.aliases() : new String[0];
    }

    @NonNull
    private List<Field> getFields(@Nullable final Class<?> type) {
        final List<Field> fields = new ArrayList<>();

        if (type != null && !Object.class.equals(type)) {
            for (final Field field : type.getDeclaredFields()) {
                final int modifiers = field.getModifiers();

                // skip ignored fields
                if (field.getAnnotation(JsonApiIgnore.class) != null) {
                    continue;
                }
                // skip static and transient fields
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }

                // skip fields we don't support
                final Class<?> fieldType = field.getType();
                final Class<?> fieldCollectionType = getFieldCollectionType(field.getGenericType());
                if (!isSupportedType(fieldType) && !supports(fieldType) && !supports(fieldCollectionType)) {
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

    @Nullable
    private Class<?> getFieldCollectionType(@NonNull final Type fieldType) {
        if (Collection.class.isAssignableFrom(JsonApiUtils.getRawType(fieldType))) {
            if (fieldType instanceof ParameterizedType) {
                return JsonApiUtils.getRawType(((ParameterizedType) fieldType).getActualTypeArguments()[0]);
            }
        }
        return null;
    }

    @NonNull
    private String getFieldName(@NonNull final Field field) {
        final JsonApiAttribute attr = field.getAnnotation(JsonApiAttribute.class);
        return attr != null && attr.name().length() > 0 ? attr.name() : field.getName();
    }

    private boolean isSupportedType(@NonNull final Class<?> type) {
        // check configured TypeConverters
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                return true;
            }
        }

        // is it a native type?
        return type.isAssignableFrom(boolean.class) || type.isAssignableFrom(double.class) ||
                type.isAssignableFrom(int.class) || type.isAssignableFrom(long.class) ||
                type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Double.class) ||
                type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class) ||
                type.isAssignableFrom(String.class);
    }

    @Nullable
    private Object convertToJsonValue(@Nullable final Object raw) {
        if (raw == null) {
            return null;
        }

        final Class<?> type = raw.getClass();

        // utilize configured TypeConverters first
        for (final TypeConverter converter : mConverters) {
            if (converter.supports(type)) {
                return converter.toString(raw);
            }
        }

        // just return native types
        return raw;
    }

    @Nullable
    private Object convertFromJSONObject(@NonNull final JSONObject json, @NonNull final String name,
                                         @NonNull final Class<?> type) throws JSONException, IllegalAccessException {
        // utilize configured TypeConverters first
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                return converter.fromString(json.optString(name, null));
            }
        }

        // handle native types
        if (type.isAssignableFrom(double.class)) {
            return json.getDouble(name);
        } else if (type.isAssignableFrom(int.class)) {
            return json.getInt(name);
        } else if (type.isAssignableFrom(long.class)) {
            return json.getLong(name);
        } else if (type.isAssignableFrom(boolean.class)) {
            return json.getBoolean(name);
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Double.class) ||
                type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class) ||
                type.isAssignableFrom(String.class)) {
            final String value = json.optString(name, null);
            try {
                if (type.isAssignableFrom(Boolean.class)) {
                    return Boolean.valueOf(value);
                } else if (type.isAssignableFrom(Double.class)) {
                    return Double.valueOf(value);
                } else if (type.isAssignableFrom(Integer.class)) {
                    return Integer.valueOf(value);
                } else if (type.isAssignableFrom(Long.class)) {
                    return Long.valueOf(value);
                } else if (type.isAssignableFrom(String.class)) {
                    return value;
                }
            } catch (final Exception e) {
                return null;
            }
        }

        return null;
    }

    static final class ObjKey {
        @NonNull
        final String mType;
        @NonNull
        final String mId;

        ObjKey(@NonNull final String type, @NonNull final String id) {
            mType = type;
            mId = id;
        }

        @Nullable
        static ObjKey create(@Nullable final JSONObject json) {
            if (json != null) {
                final String type = json.optString(JSON_DATA_TYPE, null);
                final String id = json.optString(JSON_DATA_ID, null);
                if (type != null && id != null) {
                    return new ObjKey(type, id);
                }
            }

            return null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ObjKey)) {
                return false;
            }

            final ObjKey that = (ObjKey) o;
            return mType.equals(that.mType) && mId.equals(that.mId);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {mType, mId});
        }
    }
}
