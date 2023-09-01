package org.ccci.gto.android.common.jsonapi.model

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPlaceholder
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType

@JsonApiType(ModelChild.TYPE)
class ModelChild(
    id: Int? = null,
    var name: String? = null,
    var age: Long? = null,
) : ModelBase(id) {
    companion object {
        const val TYPE = "child"
    }

    @JsonApiPlaceholder
    var isPlaceholder = false

    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        !super.equals(other) -> false
        other !is ModelChild -> false
        name != other.name -> false
        age != other.age -> false
        else -> true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (age?.hashCode() ?: 0)
        return result
    }
}
