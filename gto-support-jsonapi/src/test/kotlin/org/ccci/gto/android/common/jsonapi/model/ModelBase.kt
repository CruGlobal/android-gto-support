package org.ccci.gto.android.common.jsonapi.model

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate

abstract class ModelBase(
    @JvmField
    @JsonApiId
    var id: Int? = null,
) {
    @JvmField
    @JsonApiIgnore
    var postCreateCalled = false

    @JsonApiPostCreate
    fun finalPostCreate() {
        check(!postCreateCalled) { "post create already called!!!" }
        postCreateCalled = true
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        other == null || this::class != other::class -> false
        other !is ModelBase -> false
        id != other.id -> false
        else -> true
    }

    override fun hashCode() = id ?: 0
}
