package org.ccci.gto.android.common.jsonapi.model

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType

@JsonApiType(value = ModelSimple.TYPE, aliases = [ModelSimple.ALIAS1, ModelSimple.ALIAS2])
class ModelSimple(id: Int? = null) : ModelBase(id) {
    companion object {
        const val TYPE = "simple"
        const val ALIAS1 = "aliased"
        const val ALIAS2 = "simple_alias"
        const val NOTALIAS = "notsimple"
    }
}
