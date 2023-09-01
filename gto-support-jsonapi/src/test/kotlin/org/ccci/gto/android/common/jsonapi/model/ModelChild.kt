package org.ccci.gto.android.common.jsonapi.model

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType

@JsonApiType(ModelChild.TYPE)
class ModelChild(
    id: Int? = null,
    var name: String? = null,
    var age: Long = 0,
) : ModelBase(id) {
    companion object {
        const val TYPE = "child"
    }
}
