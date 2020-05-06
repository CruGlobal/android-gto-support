package org.ccci.gto.android.common.jsonapi.model;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;

@JsonApiType(value = ModelSimple.TYPE, aliases = {ModelSimple.ALIAS1, ModelSimple.ALIAS2})
public final class ModelSimple extends ModelBase {
    public static final String TYPE = "simple";
    public static final String ALIAS1 = "aliased";
    public static final String ALIAS2 = "simple_alias";
    public static final String NOTALIAS = "notsimple";

    public ModelSimple() {}

    public ModelSimple(final int id) {
        mId = id;
    }
}
