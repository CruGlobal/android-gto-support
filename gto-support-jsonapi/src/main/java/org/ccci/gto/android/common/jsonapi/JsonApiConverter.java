package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.converter.TypeConverter;
import org.ccci.gto.android.common.jsonapi.model.JsonApiError;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.util.Includes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_DETAIL;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_STATUS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ATTRIBUTES;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ID;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_RELATIONSHIPS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_TYPE;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_ERRORS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_INCLUDED;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_META;
import static org.ccci.gto.android.common.util.CollectionUtils.newCollection;
import static org.ccci.gto.android.common.util.NumberUtils.toInteger;

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
    private final Map<Class<?>, FieldInfo> mIdField = new HashMap<>();
    private final Map<Class<?>, List<FieldInfo>> mFields = new HashMap<>();

    JsonApiConverter(@NonNull final List<Class<?>> classes, @NonNull final List<TypeConverter<?>> converters) {
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
            final List<FieldInfo> fields = getFields(c);
            for (final Iterator<FieldInfo> i = fields.iterator(); i.hasNext(); ) {
                final FieldInfo field = i.next();
                if (field.isId()) {
                    if (mIdField.containsKey(c)) {
                        throw new IllegalArgumentException("Class " + c + " has more than one @JsonApiId defined");
                    }
                    mIdField.put(c, field);
                }
            }
            mFields.put(c, fields);

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
        return toJson(obj, Options.builder().includeAll().build());
    }

    @NonNull
    public String toJson(@NonNull final JsonApiObject<?> obj, @NonNull final Options options) {
        final Includes includes = options.mIncludes;
        try {
            final JSONObject json = new JSONObject();
            final Map<ObjKey, JSONObject> related = new HashMap<>();
            final List<JSONObject> anonymousRelated =
                    options.mIncludeObjectsWithNoId ? new ArrayList<JSONObject>() : null;
            if (obj.hasErrors()) {
                final JSONArray errors = new JSONArray();
                for (final JsonApiError error : obj.getErrors()) {
                    errors.put(errorToJson(error));
                }
                json.put(JSON_ERRORS, errors);
            } else if (obj.isSingle()) {
                final Object resource = obj.getDataSingle();
                if (resource == null) {
                    json.put(JSON_DATA, JSONObject.NULL);
                } else {
                    json.put(JSON_DATA, resourceToJson(resource, options, includes, related, anonymousRelated));
                }
            } else {
                final JSONArray dataArr = new JSONArray();
                for (final Object resource : obj.getData()) {
                    dataArr.put(resourceToJson(resource, options, includes, related, anonymousRelated));
                }
                json.put(JSON_DATA, dataArr);
            }

            // include related objects if there are any
            if (related.size() > 0 || (anonymousRelated != null && !anonymousRelated.isEmpty())) {
                final JSONArray included = new JSONArray(related.values());
                if (anonymousRelated != null) {
                    for (final JSONObject object : anonymousRelated) {
                        included.put(object);
                    }
                }
                json.put(JSON_INCLUDED, included);
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
        if (jsonObject.has(JSON_ERRORS)) {
            // {errors: []}
            output = JsonApiObject.error();
            final JSONArray errors = jsonObject.optJSONArray(JSON_ERRORS);
            if (errors != null) {
                for (int i = 0; i < errors.length(); i++) {
                    final JsonApiError error = errorFromJson(errors.optJSONObject(i));
                    if (error != null) {
                        output.addError(error);
                    }
                }
            }
        } else if (jsonObject.has(JSON_DATA)) {
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

    @NonNull
    private JSONObject errorToJson(@NonNull final JsonApiError error) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_ERROR_DETAIL, error.getDetail());
        final Integer status = error.getStatus();
        if (status != null) {
            json.put(JSON_ERROR_STATUS, status.toString());
        }
        return json;
    }

    @Nullable
    private JsonApiError errorFromJson(@Nullable final JSONObject json) {
        if (json != null) {
            final JsonApiError error = new JsonApiError();
            error.setDetail(json.optString(JSON_ERROR_DETAIL));
            error.setStatus(toInteger(json.optString(JSON_ERROR_STATUS), null));
            return error;
        }
        return null;
    }

    /**
     * @param resource
     * @param include  the relationships to include related objects for.
     * @param related
     * @return
     * @throws JSONException
     */
    @Nullable
    @SuppressWarnings("checkstyle:RightCurly")
    private JSONObject resourceToJson(@Nullable final Object resource, @NonNull final Options options,
                                      @NonNull final Includes include, @NonNull final Map<ObjKey, JSONObject> related,
                                      @Nullable final List<JSONObject> anonymousRelated)
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
        final FieldInfo idField = mIdField.get(clazz);
        if (idField != null) {
            json.put(JSON_DATA_ID, convertToJsonValue(resource, idField));
        }

        // process all fields
        final Fields fields = options.getFields(type);
        for (final FieldInfo field : mFields.get(clazz)) {
            // skip id fields, we already handled them)
            if (field.isId()) {
                continue;
            }

            // skip fields we are not including
            final String attrName = field.getAttrName();
            if (!fields.include(attrName)) {
                continue;
            }

            // get some common attributes about the field
            final Class<?> fieldType = field.getType();
            final Class<?> fieldArrayType = field.getArrayType();
            final Class<?> fieldCollectionType = field.getCollectionType();

            // is this a relationship?
            if (supports(fieldType)) {
                try {
                    final JSONObject relatedObj =
                            resourceToJson(field.mField.get(resource), options, include.descendant(attrName), related,
                                           anonymousRelated);
                    final ObjKey key = ObjKey.create(relatedObj);
                    if (key != null) {
                        final JSONObject reference =
                                new JSONObject(relatedObj, new String[] {JSON_DATA_TYPE, JSON_DATA_ID});
                        relationships.put(attrName, new JSONObject(singletonMap(JSON_DATA, reference)));

                        if (include.include(attrName)) {
                            related.put(key, relatedObj);
                        }
                    } else if (relatedObj != null && anonymousRelated != null && include.include(attrName)) {
                        anonymousRelated.add(relatedObj);
                    }
                } catch (final IllegalAccessException ignored) {
                }
                continue;
            } else if (supports(fieldArrayType)) {
                final JSONArray objs = new JSONArray();
                try {
                    final Object[] col = (Object[]) field.mField.get(resource);
                    if (col != null) {
                        for (final Object obj : col) {
                            final JSONObject relatedObj =
                                    resourceToJson(obj, options, include.descendant(attrName), related,
                                                   anonymousRelated);
                            final ObjKey key = ObjKey.create(relatedObj);
                            if (key != null) {
                                objs.put(new JSONObject(relatedObj, new String[] {JSON_DATA_TYPE, JSON_DATA_ID}));

                                if (include.include(attrName)) {
                                    related.put(key, relatedObj);
                                }
                            } else if (anonymousRelated != null && relatedObj != null && include.include(attrName)) {
                                anonymousRelated.add(relatedObj);
                            }
                        }
                    }
                } catch (final IllegalAccessException ignored) {
                }
                relationships.put(attrName, new JSONObject(singletonMap(JSON_DATA, objs)));
                continue;
            } else if (supports(fieldCollectionType)) {
                final JSONArray objs = new JSONArray();
                try {
                    final Collection col = (Collection) field.mField.get(resource);
                    if (col != null) {
                        for (final Object obj : col) {
                            final JSONObject relatedObj =
                                    resourceToJson(obj, options, include.descendant(attrName), related,
                                                   anonymousRelated);
                            final ObjKey key = ObjKey.create(relatedObj);
                            if (key != null) {
                                objs.put(new JSONObject(relatedObj, new String[] {JSON_DATA_TYPE, JSON_DATA_ID}));

                                if (include.include(attrName)) {
                                    related.put(key, relatedObj);
                                }
                            } else if (anonymousRelated != null && relatedObj != null && include.include(attrName)) {
                                anonymousRelated.add(relatedObj);
                            }
                        }
                    }
                } catch (final IllegalAccessException ignored) {
                }
                relationships.put(attrName, new JSONObject(singletonMap(JSON_DATA, objs)));
                continue;
            }

            // skip null values
            final Object value = convertToJsonValue(resource, field);
            if (value == null) {
                continue;
            }

            // everything else is a regular attribute
            attributes.put(attrName, value);
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
    private <E> E[] resourcesFromJson(@Nullable final JSONArray json, @NonNull final Class<E> type,
                                      @NonNull final Map<ObjKey, Object> objects) {
        @SuppressWarnings("unchecked")
        final E[] array = (E[]) Array.newInstance(type, json != null ? json.length() : 0);

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                array[i] = resourceFromJson(json.optJSONObject(i), type, objects);
            }
        }

        return array;
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
                resources.add(resourceFromJson(json.optJSONObject(i), type, objects));
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
        for (final FieldInfo field : mFields.get(type)) {
            final String attrName = field.getAttrName();
            final Class<?> fieldType = field.getType();
            final Class<?> fieldArrayType = field.getArrayType();
            final Class<?> fieldCollectionType = field.getCollectionType();

            try {
                // handle id fields
                if (field.isId()) {
                    field.mField.set(instance, convertFromJSONObject(json, JSON_DATA_ID, fieldType));
                }
                // handle relationships
                else if (supports(fieldType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(attrName);
                        if (related != null) {
                            field.mField.set(instance,
                                             resourceFromJson(related.optJSONObject(JSON_DATA), fieldType, objects));
                        }
                    }
                }
                // handle arrays of relationships
                else if (fieldType.isArray() && supports(fieldArrayType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(attrName);
                        if (related != null) {
                            field.mField.set(instance,
                                             resourcesFromJson(related.optJSONArray(JSON_DATA), fieldArrayType,
                                                               objects));
                        }
                    }
                }
                // handle collections of relationships
                else if (supports(fieldCollectionType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(attrName);
                        if (related != null) {
                            field.mField.set(instance,
                                             resourcesFromJson(related.optJSONArray(JSON_DATA), fieldCollectionType,
                                                               (Class<? extends Collection>) fieldType, objects));
                        }
                    }
                }
                // anything else is an attribute
                else {
                    if (attributes != null) {
                        field.mField.set(instance, convertFromJSONObject(attributes, attrName, fieldType));
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
    private List<FieldInfo> getFields(@Nullable final Class<?> type) {
        final List<FieldInfo> fields = new ArrayList<>();

        if (type != null && !Object.class.equals(type)) {
            for (final Field field : type.getDeclaredFields()) {
                // skip ignored fields
                if (field.getAnnotation(JsonApiIgnore.class) != null) {
                    continue;
                }

                // skip static and transient fields
                final int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }

                // skip fields we don't support
                final FieldInfo info = new FieldInfo(field);
                final Class<?> fieldType = info.getType();
                final Class<?> fieldArrayType = info.getArrayType();
                final Class<?> fieldCollectionType = info.getCollectionType();
                if (!(isSupportedType(fieldType) || (fieldType.isArray() && isSupportedType(fieldArrayType)) ||
                        supports(fieldCollectionType))) {
                    continue;
                }

                // set field as accessible and track it
                field.setAccessible(true);
                fields.add(info);
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
    private String getAttrName(@NonNull final Field field) {
        final JsonApiAttribute attr = field.getAnnotation(JsonApiAttribute.class);
        return attr != null && attr.name().length() > 0 ? attr.name() : field.getName();
    }

    private boolean isSupportedType(@NonNull final Class<?> type) {
        // check if this is a supported model type
        if (supports(type)) {
            return true;
        }

        // check configured TypeConverters
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                return true;
            }
        }

        // is it a native type?
        return boolean.class.equals(type) || double.class.equals(type) || int.class.equals(type) ||
                long.class.equals(type) || Boolean.class.equals(type) || Double.class.equals(type) ||
                Integer.class.equals(type) || Long.class.equals(type) || String.class.equals(type) ||
                JSONObject.class.equals(type) || JSONArray.class.equals(type);
    }

    @Nullable
    private Object convertToJsonValue(@NonNull final Object resource, @NonNull final FieldInfo field)
            throws JSONException {
        // get the value from the field
        try {
            return convertToJsonValue(field.mField.get(resource));
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    private Object convertToJsonValue(@Nullable final Object raw) throws JSONException {
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

        // handle array values
        if (type.isArray()) {
            return convertArrayToJsonValue(raw);
        }

        // just return native types
        return raw;
    }

    @NonNull
    private JSONArray convertArrayToJsonValue(@NonNull final Object raw) throws JSONException {
        final JSONArray array = new JSONArray();
        final int length = Array.getLength(raw);
        for (int i = 0; i < length; i++) {
            array.put(i, convertToJsonValue(Array.get(raw, i)));
        }

        return array;
    }

    @Nullable
    private Object convertFromJSONArray(@NonNull final JSONArray json, @NonNull final Class<?> arrayType)
            throws JSONException {
        final Object array = Array.newInstance(arrayType, json.length());
        for (int i = 0; i < json.length(); i++) {
            Array.set(array, i, convertFromJSONArray(json, i, arrayType));
        }
        return array;
    }

    @Nullable
    private Object convertFromJSONArray(@NonNull final JSONArray json, final int index, @NonNull final Class<?> type)
            throws JSONException {
        // utilize configured TypeConverters first
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                final String value = !json.isNull(index) ? json.optString(index, null) : null;
                return converter.fromString(value);
            }
        }

        // handle native types
        if (type.isAssignableFrom(double.class)) {
            return json.getDouble(index);
        } else if (type.isAssignableFrom(int.class)) {
            return json.getInt(index);
        } else if (type.isAssignableFrom(long.class)) {
            return json.getLong(index);
        } else if (type.isAssignableFrom(boolean.class)) {
            return json.getBoolean(index);
        } else if (type.isAssignableFrom(JSONObject.class)) {
            return json.getJSONObject(index);
        } else if (type.isAssignableFrom(JSONArray.class)) {
            return json.getJSONArray(index);
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Double.class) ||
                type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class) ||
                type.isAssignableFrom(String.class)) {
            final String value = !json.isNull(index) ? json.optString(index, null) : null;
            if (value == null) {
                return null;
            }
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

    @Nullable
    private Object convertFromJSONObject(@NonNull final JSONObject json, @NonNull final String name,
                                         @NonNull final Class<?> type) throws JSONException {
        // utilize configured TypeConverters first
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                final String value = !json.isNull(name) ? json.optString(name, null) : null;
                return converter.fromString(value);
            }
        }

        // handle array types
        if (type.isArray() && type.getComponentType() != null) {
            return convertFromJSONArray(json.getJSONArray(name), type.getComponentType());
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
        } else if (type.isAssignableFrom(JSONObject.class)) {
            return json.getJSONObject(name);
        } else if (type.isAssignableFrom(JSONArray.class)) {
            return json.getJSONArray(name);
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Double.class) ||
                type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class) ||
                type.isAssignableFrom(String.class)) {
            final String value = !json.isNull(name) ? json.optString(name, null) : null;
            if (value == null) {
                return null;
            }
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

    static final class FieldInfo {
        @NonNull
        final Field mField;

        @Nullable
        private Class<?> mCollectionType;
        private boolean mCollectionTypeResolved = false;

        private boolean mIsId;
        private boolean mIsIdResolved = false;

        @Nullable
        private String mAttrName;

        FieldInfo(@NonNull final Field field) {
            mField = field;
        }

        @NonNull
        Class<?> getType() {
            return mField.getType();
        }

        @Nullable
        Class<?> getArrayType() {
            return getType().getComponentType();
        }

        @Nullable
        Class<?> getCollectionType() {
            // resolve the collection type if we haven't resolved it already
            if (!mCollectionTypeResolved) {
                final Type type = mField.getGenericType();
                if (Collection.class.isAssignableFrom(JsonApiUtils.getRawType(type))) {
                    if (type instanceof ParameterizedType) {
                        mCollectionType =
                                JsonApiUtils.getRawType(((ParameterizedType) type).getActualTypeArguments()[0]);
                    }
                }
                mCollectionTypeResolved = true;
            }

            return mCollectionType;
        }

        boolean isId() {
            if (!mIsIdResolved) {
                mIsId = mField.getAnnotation(JsonApiId.class) != null;
                mIsIdResolved = true;
            }

            return mIsId;
        }

        @NonNull
        String getAttrName() {
            if (mAttrName == null) {
                final JsonApiAttribute attr = mField.getAnnotation(JsonApiAttribute.class);
                mAttrName = attr != null && attr.name().length() > 0 ? attr.name() : mField.getName();
            }

            return mAttrName;
        }
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
                final String type = !json.isNull(JSON_DATA_TYPE) ? json.optString(JSON_DATA_TYPE, null) : null;
                final String id = !json.isNull(JSON_DATA_ID) ? json.optString(JSON_DATA_ID, null) : null;
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

    public static final class Options {
        @NonNull
        final Includes mIncludes;
        final boolean mIncludeObjectsWithNoId;
        @NonNull
        final Map<String, Fields> mFields;

        Options(@NonNull final Includes includes, final boolean includeObjectsWithNoId,
                final Map<String, Fields> fields) {
            mIncludes = includes;
            mIncludeObjectsWithNoId = includeObjectsWithNoId;
            mFields = fields;
        }

        @NonNull
        public Options merge(@Nullable final Options options) {
            if (options == null) {
                return this;
            }

            // default the map to our current fields, then merge in fields from the provided options.
            final Map<String, Fields> fields = new ArrayMap<>();
            fields.putAll(mFields);
            for (final String key : options.mFields.keySet()) {
                fields.put(key, options.mFields.get(key).merge(fields.get(key)));
            }

            return new Options(mIncludes.merge(options.mIncludes),
                               mIncludeObjectsWithNoId || options.mIncludeObjectsWithNoId, fields);
        }

        @NonNull
        public static Builder builder() {
            return new Builder();
        }

        @NonNull
        public static Options include(@NonNull final String... include) {
            return builder().include(include).build();
        }

        @NonNull
        Fields getFields(@Nullable final String type) {
            Fields fields = mFields.get(type);
            return fields != null ? fields : Fields.ALL;
        }

        public static final class Builder {
            private List<String> mIncludes = null;
            private boolean mIncludeObjectsWithNoId = false;
            private Map<String, Set<String>> mFields = new ArrayMap<>();

            @NonNull
            public Builder includeAll() {
                mIncludes = null;
                return this;
            }

            @NonNull
            public Builder include(@NonNull final String... include) {
                if (mIncludes == null) {
                    mIncludes = new ArrayList<>();
                }

                Collections.addAll(mIncludes, include);
                return this;
            }

            public Builder includeObjectsWithNoId(final boolean state) {
                mIncludeObjectsWithNoId = state;
                return this;
            }

            public Builder fields(@Nullable final String type, @NonNull final String... fields) {
                Set<String> currentFields = mFields.get(type);
                if (currentFields == null) {
                    currentFields = new HashSet<>();
                }
                Collections.addAll(currentFields, fields);
                mFields.put(type, currentFields);

                return this;
            }

            @NonNull
            public Options build() {
                // build out the Fields data structure
                final Map<String, Fields> fields = new ArrayMap<>();
                for (final String type : mFields.keySet()) {
                    final Set<String> values = mFields.get(type);
                    fields.put(type, new Fields(values));
                }

                return new Options(new Includes(mIncludes), mIncludeObjectsWithNoId, fields);
            }
        }
    }

    static final class Fields {
        static final Fields ALL = new Fields(null);

        @Nullable
        private final Set<String> mFields;

        Fields(@Nullable final Collection<String> fields) {
            mFields = fields != null ? new HashSet<>(fields) : null;
        }

        Fields merge(@Nullable final Fields fields) {
            if (fields == null) {
                return this;
            }

            if (mFields == null) {
                return this;
            } else if (fields.mFields == null) {
                return fields;
            } else if (mFields.isEmpty()) {
                return fields;
            } else if (fields.mFields.isEmpty()) {
                return this;
            } else {
                final List<String> values = new ArrayList<>(mFields);
                values.addAll(fields.mFields);
                return new Fields(values);
            }
        }

        boolean include(@NonNull final String field) {
            return mFields == null || mFields.contains(field);
        }
    }
}
