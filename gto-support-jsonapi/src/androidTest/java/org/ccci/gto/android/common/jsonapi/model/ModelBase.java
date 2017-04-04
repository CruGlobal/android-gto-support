package org.ccci.gto.android.common.jsonapi.model;

import android.support.annotation.Nullable;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;

public abstract class ModelBase {
    @Nullable
    @JsonApiId
    public Integer mId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ModelBase modelBase = (ModelBase) o;

        return mId != null ? mId.equals(modelBase.mId) : modelBase.mId == null;
    }

    @Override
    public int hashCode() {
        return mId != null ? mId : Integer.MAX_VALUE;
    }
}
