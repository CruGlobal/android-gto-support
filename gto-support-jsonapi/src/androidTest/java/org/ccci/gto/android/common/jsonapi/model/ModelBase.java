package org.ccci.gto.android.common.jsonapi.model;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;

public abstract class ModelBase {
    @JsonApiId
    public int mId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ModelBase modelBase = (ModelBase) o;

        return mId == modelBase.mId;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}
