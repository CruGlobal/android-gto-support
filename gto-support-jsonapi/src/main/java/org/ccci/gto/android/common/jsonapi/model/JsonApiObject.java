package org.ccci.gto.android.common.jsonapi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JsonApiObject<T> {
    public static final String MEDIA_TYPE = "application/vnd.api+json";

    public static final String JSON_DATA = "data";
    public static final String JSON_DATA_TYPE = "type";
    public static final String JSON_DATA_ID = "id";
    public static final String JSON_DATA_ATTRIBUTES = "attributes";
    public static final String JSON_DATA_RELATIONSHIPS = "relationships";
    public static final String JSON_ERRORS = "errors";
    public static final String JSON_INCLUDED = "included";
    public static final String JSON_META = "meta";

    private final boolean mSingle;

    @NonNull
    private final List<T> mData = new ArrayList<>();

    private final List<JsonApiError> mErrors = new ArrayList<>();

    @Nullable
    private JSONObject mRawMeta;

    private JsonApiObject(final boolean single) {
        mSingle = single;
    }

    protected JsonApiObject(@NonNull final JsonApiObject<T> source) {
        mSingle = source.mSingle;
        mData.addAll(source.mData);
        mErrors.addAll(source.mErrors);
        mRawMeta = source.mRawMeta;
    }

    public static <T> JsonApiObject<T> single(@Nullable final T data) {
        final JsonApiObject<T> obj = new JsonApiObject<>(true);
        obj.setData(data);
        return obj;
    }

    @SafeVarargs
    public static <T> JsonApiObject<T> of(@NonNull final T... data) {
        final JsonApiObject<T> obj = new JsonApiObject<>(false);
        obj.setData(Arrays.asList(data));
        return obj;
    }

    public static <T> JsonApiObject<T> error(@NonNull final JsonApiError... errors) {
        final JsonApiObject<T> obj = new JsonApiObject<>(false);
        obj.setErrors(Arrays.asList(errors));
        return obj;
    }

    public final boolean isSingle() {
        return mSingle;
    }

    public final List<T> getData() {
        return Collections.unmodifiableList(mData);
    }

    @Nullable
    public final T getDataSingle() {
        return mData.size() > 0 ? mData.get(0) : null;
    }

    public final void setData(@Nullable final Collection<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
    }

    public final void setData(@Nullable final T data) {
        mData.clear();
        mData.add(data);
    }

    public final void addData(final T resource) {
        mData.add(resource);
    }

    public final boolean hasErrors() {
        return !mErrors.isEmpty();
    }

    @NonNull
    public final List<JsonApiError> getErrors() {
        return Collections.unmodifiableList(mErrors);
    }

    public final void setErrors(@Nullable final Collection<JsonApiError> errors) {
        mErrors.clear();
        if (errors != null) {
            mErrors.addAll(errors);
        }
    }

    public final void addError(@NonNull final JsonApiError error) {
        mErrors.add(error);
    }

    @Nullable
    public final JSONObject getRawMeta() {
        return mRawMeta;
    }

    public final void setRawMeta(@Nullable final JSONObject meta) {
        mRawMeta = meta;
    }
}
