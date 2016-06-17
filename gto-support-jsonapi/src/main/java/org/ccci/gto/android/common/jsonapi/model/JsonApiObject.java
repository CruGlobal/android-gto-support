package org.ccci.gto.android.common.jsonapi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class JsonApiObject<T> {
    public static final String JSON_DATA = "data";
    public static final String JSON_DATA_TYPE = "type";
    public static final String JSON_DATA_ID = "id";
    public static final String JSON_DATA_ATTRIBUTES = "attributes";
    public static final String JSON_DATA_RELATIONSHIPS = "relationships";
    public static final String JSON_INCLUDED = "included";
    public static final String JSON_META = "meta";

    private final boolean mSingle;

    @NonNull
    private final List<T> mData = new ArrayList<>();

    @Nullable
    private JSONObject mRawMeta;

    private JsonApiObject(final boolean single) {
        mSingle = single;
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

    public boolean isSingle() {
        return mSingle;
    }

    public List<T> getData() {
        return Collections.unmodifiableList(mData);
    }

    @Nullable
    public T getDataSingle() {
        return mData.size() > 0 ? mData.get(0) : null;
    }

    public void setData(@Nullable final Collection<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
    }

    public void setData(@Nullable final T data) {
        mData.clear();
        mData.add(data);
    }

    public void addData(final T resource) {
        mData.add(resource);
    }

    @Nullable
    public JSONObject getRawMeta() {
        return mRawMeta;
    }

    public void setRawMeta(@Nullable final JSONObject meta) {
        mRawMeta = meta;
    }
}
