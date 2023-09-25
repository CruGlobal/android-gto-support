package org.ccci.gto.android.common.jsonapi.retrofit2

class JsonApiParams : BaseJsonApiParams<JsonApiParams>() {
    companion object {
        const val PARAM_INCLUDE = "include"
    }

    override val self get() = this
}
