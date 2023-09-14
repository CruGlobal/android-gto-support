package org.ccci.gto.android.common.jsonapi.model

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPlaceholder
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType

@JsonApiType(ModelParent.TYPE)
class ModelParent(
    id: Int? = null,
    var name: String? = null,
    var age: Long = 0,
    var children: List<ModelChild> = emptyList(),
    var favorite: ModelChild? = null, // everyone has a favorite child
    var orphans: Array<ModelChild>? = null,
) : ModelBase(id) {
    companion object {
        const val TYPE = "parent"
    }

    @JsonApiPlaceholder
    var isPlaceholder = false
}
