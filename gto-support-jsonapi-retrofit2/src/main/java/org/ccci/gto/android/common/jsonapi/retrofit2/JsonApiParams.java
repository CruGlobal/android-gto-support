package org.ccci.gto.android.common.jsonapi.retrofit2;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;

public class JsonApiParams extends ForwardingMap<String, String> {
    @VisibleForTesting
    static final String PARAM_INCLUDE = "include";

    private final Map<String, String> mParams = new HashMap<>();

    @Override
    protected Map<String, String> delegate() {
        return mParams;
    }

    public void clearIncludes() {
        remove(PARAM_INCLUDE);
    }

    public void setIncludes(@Nullable final String... paths) {
        if (paths != null) {
            put(PARAM_INCLUDE, TextUtils.join(",", paths));
        } else {
            remove(PARAM_INCLUDE);
        }
    }
}
