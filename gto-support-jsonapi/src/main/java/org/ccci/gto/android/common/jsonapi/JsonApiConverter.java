package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPlaceholder;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static java.util.Collections.singletonMap;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_DETAIL;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_META;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_SOURCE;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_STATUS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.JSON_ERROR_TITLE;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiError.Source.JSON_ERROR_SOURCE_POINTER;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ATTRIBUTES;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_ID;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_RELATIONSHIPS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_DATA_TYPE;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_ERRORS;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_INCLUDED;
import static org.ccci.gto.android.common.jsonapi.model.JsonApiObject.JSON_META;
import static org.ccci.gto.android.common.jsonapi.util.CollectionUtils.newCollection;
import static org.ccci.gto.android.common.jsonapi.util.NumberUtils.toInteger;

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
    private final Map<Class<?>, FieldInfo> mPlaceholderField = new HashMap<>();
    private final Map<Class<?>, List<MethodInfo>> mPostCreateMethod = new HashMap<>();
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

            // store this type and any aliases
            mTypes.put(type, c);
            for (final String alias : getResourceTypeAliases(c)) {
                // throw an exception if the specified alias is already defined
                if (mTypes.containsKey(alias)) {
                    throw new IllegalArgumentException(
                            "Duplicate @JsonApiType(\"" + alias + "\") shared by " + mTypes.get(alias) + " and " + c);
                }

                mTypes.put(alias, c);
            }

            // initialize Fields and Methods for the class
            initFields(c);
            initMethods(c);
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
        final Map<ObjKey, ObjValue> objects = new HashMap<>();
        final JSONArray included = jsonObject.optJSONArray(JSON_INCLUDED);
        if (included != null) {
            //noinspection unchecked
            resourcesFromJson(included, Object.class, Collection.class, false, objects);
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
                output.setData(resourcesFromJson(dataArray, type, Collection.class, false, objects));
            }
            // {data: null} or {data: {}}
            else {
                output = JsonApiObject.single(null);
                final T resource = resourceFromJson(jsonObject.optJSONObject(JSON_DATA), type, false, objects);
                if (resource != null) {
                    output.setData(resource);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }

        // call any post-create methods
        for (final ObjValue obj : objects.values()) {
            triggerPostCreate(obj);
        }

        // pass the JSONApi meta object back as-is
        output.setRawMeta(jsonObject.optJSONObject(JSON_META));

        return output;
    }

    private void initFields(@NonNull final Class<?> clazz) {
        final List<FieldInfo> fields = getFields(clazz);
        for (final Iterator<FieldInfo> i = fields.iterator(); i.hasNext();) {
            final FieldInfo field = i.next();
            if (field.isId()) {
                if (mIdField.containsKey(clazz)) {
                    throw new IllegalArgumentException("Class " + clazz + " has more than one @JsonApiId defined");
                }
                mIdField.put(clazz, field);
            }
            if (field.isPlaceholder()) {
                if (mPlaceholderField.containsKey(clazz)) {
                    throw new IllegalArgumentException("Class " + clazz + " has more than one placeholder defined");
                }
                final Class<?> fieldType = field.getType();
                if (!Boolean.class.equals(fieldType) && !boolean.class.equals(fieldType)) {
                    throw new IllegalArgumentException(
                            "Class " + clazz + " has an unsupported placeholder field type " + fieldType);
                }
                mPlaceholderField.put(clazz, field);
                i.remove();
            }
        }
        mFields.put(clazz, fields);
    }

    private void initMethods(@NonNull final Class<?> clazz) {
        for (final MethodInfo method : getMethods(clazz)) {
            if (method.mIsPostCreate) {
                List<MethodInfo> postCreateMethods = mPostCreateMethod.get(clazz);
                if (postCreateMethods == null) {
                    postCreateMethods = new ArrayList<>();
                    mPostCreateMethod.put(clazz, postCreateMethods);
                }

                if (method.mMethod.getParameterTypes().length > 0) {
                    throw new IllegalArgumentException(
                            "@JsonApiPostCreate annotated method '" + method.mMethod + "' cannot have any parameters");
                }
                if (method.throwsCheckedException()) {
                    throw new IllegalArgumentException("@JsonApiPostCreate annotated method '" + method.mMethod +
                                                               "' cannot throw a checked exception");
                }
                if (method.isStatic()) {
                    throw new IllegalArgumentException(
                            "@JsonApiPostCreate annotated method '" + method.mMethod + "' cannot be static");
                }
                if (!method.isEffectivelyFinal()) {
                    throw new IllegalArgumentException(
                            "@JsonApiPostCreate annotated method '" + method.mMethod + "' must be effectively final");
                }

                postCreateMethods.add(method);
            }
        }
    }

    @NonNull
    private JSONObject errorToJson(@NonNull final JsonApiError error) throws JSONException {
        final JSONObject json = new JSONObject();
        final Integer status = error.getStatus();
        if (status != null) {
            json.put(JSON_ERROR_STATUS, status.toString());
        }
        json.put(JSON_ERROR_TITLE, error.getTitle());
        json.put(JSON_ERROR_DETAIL, error.getDetail());
        json.put(JSON_ERROR_SOURCE, errorSourceToJson(error.getSource()));
        json.put(JSON_ERROR_META, error.getRawMeta());
        return json;
    }

    @Nullable
    private JSONObject errorSourceToJson(@Nullable final JsonApiError.Source source) throws JSONException {
        if (source != null) {
            final JSONObject json = new JSONObject();
            json.put(JSON_ERROR_SOURCE_POINTER, source.getPointer());
            return json;
        }
        return null;
    }

    @Nullable
    private JsonApiError errorFromJson(@Nullable final JSONObject json) {
        if (json != null) {
            final JsonApiError error = new JsonApiError();
            error.setStatus(toInteger(json.optString(JSON_ERROR_STATUS, null)));
            error.setTitle(json.optString(JSON_ERROR_TITLE, null));
            error.setDetail(json.optString(JSON_ERROR_DETAIL, null));
            error.setSource(errorSourceFromJson(json.optJSONObject(JSON_ERROR_SOURCE)));
            error.setRawMeta(json.optJSONObject(JSON_ERROR_META));
            return error;
        }
        return null;
    }

    @Nullable
    private JsonApiError.Source errorSourceFromJson(@Nullable final JSONObject json) {
        if (json != null) {
            final JsonApiError.Source source = new JsonApiError.Source();
            source.setPointer(json.optString(JSON_ERROR_SOURCE_POINTER, null));
            return source;
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

            // skip fields that shouldn't be serialized
            if (!field.serialize()) {
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

            // skip null values if we aren't serializing nulls for this type
            final Object value = convertToJsonValue(resource, field);
            if (value == null && !options.serializeNullAttributes(type)) {
                continue;
            }

            // everything else is a regular attribute
            attributes.put(attrName, value != null ? value : JSONObject.NULL);
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
                                      final boolean placeholder, @NonNull final Map<ObjKey, ObjValue> objects) {
        @SuppressWarnings("unchecked")
        final E[] array = (E[]) Array.newInstance(type, json != null ? json.length() : 0);

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                array[i] = resourceFromJson(json.optJSONObject(i), type, placeholder, objects);
            }
        }

        return array;
    }

    @NonNull
    private <E, T extends Collection<E>> T resourcesFromJson(@Nullable final JSONArray json,
                                                             @NonNull final Class<E> type,
                                                             @NonNull final Class<T> collectionType,
                                                             final boolean placeholder,
                                                             @NonNull final Map<ObjKey, ObjValue> objects) {
        final T resources = newCollection(collectionType);
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                resources.add(resourceFromJson(json.optJSONObject(i), type, placeholder, objects));
            }
        }
        return resources;
    }

    @Nullable
    @SuppressWarnings("checkstyle:RightCurly")
    private <E> E resourceFromJson(@Nullable final JSONObject json, @NonNull final Class<E> expectedType,
                                   final boolean placeholder, @NonNull final Map<ObjKey, ObjValue> objects) {
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
        ObjValue<E> value = null;
        if (key != null) {
            //noinspection unchecked
            value = objects.get(key);
        }
        // no object found, create a new instance
        if (value == null) {
            try {
                //noinspection unchecked
                value = new ObjValue<>((E) type.newInstance());
                if (key != null) {
                    objects.put(key, value);
                }

                // mark the new object as a placeholder
                final FieldInfo placeholderField = mPlaceholderField.get(type);
                if (placeholderField != null) {
                    placeholderField.mField.set(value.mObject, true);
                }
            } catch (final Exception e) {
                return null;
            }
        }
        final E instance = value.mObject;

        // populate fields
        final JSONObject attributes = json.optJSONObject(JSON_DATA_ATTRIBUTES);
        final JSONObject relationships = json.optJSONObject(JSON_DATA_RELATIONSHIPS);
        for (final FieldInfo field : mFields.get(type)) {
            // skip fields that shouldn't be deserialized
            if (!field.deserialize()) {
                continue;
            }

            final String attrName = field.getAttrName();
            final Class<?> fieldType = field.getType();
            final Class<?> fieldArrayType = field.getArrayType();
            final Class<?> fieldCollectionType = field.getCollectionType();

            try {
                // handle id fields
                if (field.isId()) {
                    field.mField.set(instance, convertFromJSONObject(json, JSON_DATA_ID, field));
                }
                // handle relationships
                else if (supports(fieldType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(attrName);
                        if (related != null) {
                            field.mField.set(instance,
                                             resourceFromJson(related.optJSONObject(JSON_DATA), fieldType, true,
                                                              objects));
                        }
                    }
                }
                // handle arrays of relationships
                else if (fieldType.isArray() && supports(fieldArrayType)) {
                    if (relationships != null) {
                        final JSONObject related = relationships.optJSONObject(attrName);
                        if (related != null) {
                            field.mField.set(instance,
                                             resourcesFromJson(related.optJSONArray(JSON_DATA), fieldArrayType, true,
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
                                                               (Class<? extends Collection>) fieldType, true, objects));
                        }
                    }
                }
                // anything else is an attribute
                else {
                    if (attributes != null) {
                        field.mField.set(instance, convertFromJSONObject(attributes, attrName, field));
                    }
                }
            } catch (final JSONException | IllegalAccessException ignored) {
            }
        }

        // was the full object just instantiated
        if (!placeholder) {
            // clear placeholder state
            value.mPlaceholder = false;
            final FieldInfo placeholderField = mPlaceholderField.get(type);
            if (placeholderField != null) {
                try {
                    placeholderField.mField.set(instance, false);
                } catch (final IllegalAccessException ignored) {
                }
            }

            // trigger post create method if there wasn't a key for this object
            if (key == null) {
                triggerPostCreate(value);
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
                        (Collection.class.isAssignableFrom(fieldType) && isSupportedType(fieldCollectionType)))) {
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

    private List<MethodInfo> getMethods(@Nullable final Class<?> type) {
        if (type == null || Object.class.equals(type)) {
            return Collections.emptyList();
        }

        // process all immediate declared methods
        final List<MethodInfo> methods = new ArrayList<>();
        for (final Method method : type.getDeclaredMethods()) {
            // skip synthetic and bridge methods
            if (method.isSynthetic() || method.isBridge()) {
                continue;
            }

            // only return relevant methods
            final MethodInfo info = new MethodInfo(method);
            if (info.isRelevant()) {
                method.setAccessible(true);
                methods.add(info);
            }
        }

        // process the superclass
        methods.addAll(getMethods(type.getSuperclass()));

        return methods;
    }

    private boolean isSupportedType(@Nullable final Class<?> type) {
        if (type == null) {
            return false;
        }

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
        return boolean.class.equals(type) || double.class.equals(type) || float.class.equals(type) ||
                int.class.equals(type) || long.class.equals(type) || Boolean.class.equals(type) ||
                Double.class.equals(type) || Float.class.equals(type) || Integer.class.equals(type) ||
                Long.class.equals(type) || String.class.equals(type) || JSONObject.class.equals(type) ||
                JSONArray.class.equals(type);
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

        // handle collection values
        if (raw instanceof Collection) {
            return convertCollectionToJsonValue((Collection) raw);
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

    private JSONArray convertCollectionToJsonValue(@NonNull final Collection raw) throws JSONException {
        final JSONArray array = new JSONArray();
        for (final Object elem : raw) {
            array.put(convertToJsonValue(elem));
        }
        return array;
    }

    @Nullable
    private Object convertFromJSONObject(@NonNull final JSONObject json, @NonNull final String name,
                                         @NonNull FieldInfo info) throws JSONException {
        // short-circuit if the attribute doesn't exist
        if (!json.has(name)) {
            throw new JSONException("No value for " + name);
        }

        // utilize configured TypeConverters first
        final Class<?> type = info.getType();
        for (final TypeConverter<?> converter : mConverters) {
            if (converter.supports(type)) {
                final String value = !json.isNull(name) ? json.optString(name, null) : null;
                return converter.fromString(value);
            }
        }

        // handle array types
        final Class<?> arrayElementType = info.getArrayType();
        if (type.isArray() && arrayElementType != null) {
            return convertArrayFromJSONArray(json.getJSONArray(name), arrayElementType);
        }

        // handle collection types
        final Class<?> collectionElementType = info.getCollectionType();
        if (Collection.class.isAssignableFrom(type) && collectionElementType != null) {
            return convertCollectionFromJSONArray(json.getJSONArray(name), (Class<? extends Collection>) type,
                                                  collectionElementType);
        }

        // handle native types
        if (type.isAssignableFrom(double.class)) {
            return json.getDouble(name);
        } else if (type.isAssignableFrom(float.class)) {
            return (float) json.getDouble(name);
        } else if (type.isAssignableFrom(int.class)) {
            return json.getInt(name);
        } else if (type.isAssignableFrom(long.class)) {
            return json.getLong(name);
        } else if (type.isAssignableFrom(boolean.class)) {
            return json.getBoolean(name);
        } else if (json.isNull(name)) {
            return null;
        } else if (type.isAssignableFrom(JSONObject.class)) {
            return json.getJSONObject(name);
        } else if (type.isAssignableFrom(JSONArray.class)) {
            return json.getJSONArray(name);
        } else if (type.isAssignableFrom(Boolean.class)) {
            return json.getBoolean(name);
        } else if (type.isAssignableFrom(Double.class)) {
            return json.getDouble(name);
        } else if (type.isAssignableFrom(Float.class)) {
            return (float) json.getDouble(name);
        } else if (type.isAssignableFrom(Integer.class)) {
            return json.getInt(name);
        } else if (type.isAssignableFrom(Long.class)) {
            return json.getLong(name);
        } else if (type.isAssignableFrom(String.class)) {
            return json.optString(name);
        }

        // default to null
        return null;
    }

    @NonNull
    private Object convertArrayFromJSONArray(@NonNull final JSONArray json, @NonNull final Class<?> arrayType)
            throws JSONException {
        final Object array = Array.newInstance(arrayType, json.length());
        for (int i = 0; i < json.length(); i++) {
            Array.set(array, i, convertFromJSONArray(json, i, arrayType));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private <T extends Collection> T convertCollectionFromJSONArray(
            @NonNull final JSONArray json, @NonNull final Class<T> collectionType, @NonNull final Class<?> elementType)
            throws JSONException {
        final T collection = newCollection(collectionType);
        for (int i = 0; i < json.length(); i++) {
            collection.add(convertFromJSONArray(json, i, elementType));
        }
        return collection;
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
        } else if (type.isAssignableFrom(float.class)) {
            return (float) json.getDouble(index);
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
                type.isAssignableFrom(Float.class) || type.isAssignableFrom(Integer.class) ||
                type.isAssignableFrom(Long.class) || type.isAssignableFrom(String.class)) {
            final String value = !json.isNull(index) ? json.optString(index, null) : null;
            if (value == null) {
                return null;
            }
            try {
                if (type.isAssignableFrom(Boolean.class)) {
                    return Boolean.valueOf(value);
                } else if (type.isAssignableFrom(Double.class)) {
                    return Double.valueOf(value);
                } else if (type.isAssignableFrom(Float.class)) {
                    return Float.valueOf(value);
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

    private void triggerPostCreate(@NonNull final ObjValue object) {
        // don't run post-create method for placeholder objects
        if (object.mPlaceholder) {
            return;
        }

        // short-circuit if there isn't a post-create method
        final Class<?> type = object.mObject.getClass();
        final List<MethodInfo> methods = mPostCreateMethod.get(type);
        if (methods == null || methods.isEmpty()) {
            return;
        }

        // invoke the Post-Create methods
        for (final MethodInfo method : methods) {
            try {
                method.mMethod.invoke(object.mObject);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                final Throwable t = e.getCause();
                if (t instanceof Error) {
                    throw (Error) t;
                } else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else {
                    throw new IllegalStateException(
                            "Method '" + method.mMethod + "' threw an unexpected checked exception", t);
                }
            }
        }
    }

    static final class FieldInfo {
        @NonNull
        final Field mField;
        @Nullable
        private final JsonApiAttribute mAttribute;
        @Nullable
        private final JsonApiPlaceholder mPlaceholder;

        @Nullable
        private Class<?> mCollectionType;
        private boolean mCollectionTypeResolved = false;

        private boolean mIsId;
        private boolean mIsIdResolved = false;

        @Nullable
        private String mAttrName;

        FieldInfo(@NonNull final Field field) {
            mField = field;
            mAttribute = mField.getAnnotation(JsonApiAttribute.class);
            mPlaceholder = mField.getAnnotation(JsonApiPlaceholder.class);
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

        boolean isPlaceholder() {
            return mPlaceholder != null;
        }

        @NonNull
        String getAttrName() {
            if (mAttrName == null) {
                if (mAttribute != null && mAttribute.name().length() > 0) {
                    mAttrName = mAttribute.name();
                } else if (mAttribute != null && mAttribute.value().length() > 0) {
                    mAttrName = mAttribute.value();
                } else {
                    mAttrName = mField.getName();
                }
            }

            return mAttrName;
        }

        boolean serialize() {
            return mAttribute == null || mAttribute.serialize();
        }

        boolean deserialize() {
            return mAttribute == null || mAttribute.deserialize();
        }
    }

    static final class MethodInfo {
        @NonNull
        private final Class<?> mClass;
        @NonNull
        final Method mMethod;
        final int mModifiers;
        final boolean mIsPostCreate;

        MethodInfo(@NonNull final Method method) {
            mClass = method.getDeclaringClass();
            mMethod = method;
            mModifiers = method.getModifiers();
            mIsPostCreate = method.getAnnotation(JsonApiPostCreate.class) != null;
        }

        boolean isRelevant() {
            return mIsPostCreate;
        }

        boolean isStatic() {
            return Modifier.isStatic(mModifiers);
        }

        boolean isEffectivelyFinal() {
            return Modifier.isFinal(mModifiers) || Modifier.isPrivate(mModifiers) ||
                    Modifier.isFinal(mClass.getModifiers());
        }

        boolean throwsCheckedException() {
            for (final Class<?> exceptionType : mMethod.getExceptionTypes()) {
                // skip any unchecked exceptions
                if (Error.class.isAssignableFrom(exceptionType) ||
                        RuntimeException.class.isAssignableFrom(exceptionType)) {
                    continue;
                }

                // this wasn't an unchecked exception, so it's a checked exception
                return true;
            }
            return false;
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

    static final class ObjValue<T> {
        @NonNull
        final T mObject;
        boolean mPlaceholder = true;

        ObjValue(@NonNull final T object) {
            mObject = object;
        }
    }

    public static final class Options {
        @NonNull
        final Includes mIncludes;
        final boolean mIncludeObjectsWithNoId;
        @NonNull
        private final Map<String, Fields> mFields;
        @NonNull
        final Map<String, Boolean> mSerializeNullAttributes;

        Options(@NonNull final Includes includes, final boolean includeObjectsWithNoId,
                @NonNull final Map<String, Fields> fields,
                @NonNull final Map<String, Boolean> serializeNullAttributes) {
            mIncludes = includes;
            mIncludeObjectsWithNoId = includeObjectsWithNoId;
            mFields = fields;
            mSerializeNullAttributes = serializeNullAttributes;
        }

        @NonNull
        public Options merge(@Nullable final Options options) {
            if (options == null) {
                return this;
            }

            // default the map to our current fields, then merge in fields from the provided options.
            final Map<String, Fields> fields = new HashMap<>(mFields);
            for (final String key : options.mFields.keySet()) {
                fields.put(key, options.mFields.get(key).merge(fields.get(key)));
            }

            // Merge serializeNullAttributes, preferring the options being merged in
            final Map<String, Boolean> serializeNullAttributes = new HashMap<>();
            serializeNullAttributes.putAll(mSerializeNullAttributes);
            serializeNullAttributes.putAll(options.mSerializeNullAttributes);

            return new Options(mIncludes.merge(options.mIncludes),
                               mIncludeObjectsWithNoId || options.mIncludeObjectsWithNoId, fields,
                               serializeNullAttributes);
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

        boolean serializeNullAttributes(@Nullable final String type) {
            final Boolean serialize = mSerializeNullAttributes.get(type);
            return serialize != null ? serialize : false;
        }

        public static final class Builder {
            private List<String> mIncludes = null;
            private boolean mIncludeObjectsWithNoId = false;
            private final Map<String, Set<String>> mFields = new HashMap<>();
            private final Map<String, Boolean> mSerializeNullAttributes = new HashMap<>();

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

            public Builder serializeNullAttributes(@NonNull final String type, @Nullable final Boolean state) {
                if (state == null) {
                    mSerializeNullAttributes.remove(type);
                } else {
                    mSerializeNullAttributes.put(type, state);
                }
                return this;
            }

            public Builder serializeNullAttributes(@NonNull final String type) {
                return serializeNullAttributes(type, true);
            }

            public Builder dontSerializeNullAttributes(@NonNull final String type) {
                return serializeNullAttributes(type, false);
            }

            @NonNull
            public Options build() {
                // build out the Fields data structure
                final Map<String, Fields> fields = new HashMap<>();
                for (final String type : mFields.keySet()) {
                    final Set<String> values = mFields.get(type);
                    fields.put(type, new Fields(values));
                }

                return new Options(new Includes(mIncludes), mIncludeObjectsWithNoId, fields,
                        new HashMap<>(mSerializeNullAttributes));
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
