package org.ccci.gto.android.common.jsonapi.retrofit2;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseJsonApiParams<T extends BaseJsonApiParams> extends ForwardingMap<String, String> {
    @VisibleForTesting
    static final String PARAM_INCLUDE = "include";
    private static final String PARAM_FIELDS = "fields";

    private static final Joiner INCLUDE_JOINER = Joiner.on(",").skipNulls();

    private final Map<String, String> mParams = new HashMap<>();

    @Override
    protected Map<String, String> delegate() {
        return mParams;
    }

    @NonNull
    protected abstract T self();

    @NonNull
    public final T include(final String... paths) {
        put(PARAM_INCLUDE, INCLUDE_JOINER.join(Strings.emptyToNull(get(PARAM_INCLUDE)), null, (Object[]) paths));
        return self();
    }

    @NonNull
    public final T clearIncludes() {
        remove(PARAM_INCLUDE);
        return self();
    }

    @NonNull
    public final T fields(@NonNull final String type, @NonNull final String... fields) {
        final String param = PARAM_FIELDS + "[" + type + "]";
        put(param, INCLUDE_JOINER.join(Strings.emptyToNull(get(param)), null, (Object[]) fields));
        return self();
    }

    public final T clearFields(@NonNull final String type) {
        remove(PARAM_FIELDS + "[" + type + "]");
        return self();
    }
}
