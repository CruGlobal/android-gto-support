package org.ccci.gto.android.common.jsonapi.retrofit2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;

public class JsonApiRetrofitObject<T> extends JsonApiObject<T> {
    @Nullable
    private Options mOptions;

    public JsonApiRetrofitObject(@NonNull final JsonApiObject<T> obj) {
        super(obj);
    }

    @Nullable
    public Options getOptions() {
        return mOptions;
    }

    public void setOptions(@Nullable final Options options) {
        mOptions = options;
    }
}
