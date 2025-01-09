package org.ccci.gto.android.common.jsonapi.model

import org.json.JSONObject

open class JsonApiObject<T : Any> private constructor(
    val isSingle: Boolean,
    var data: List<T> = emptyList(),
    var errors: List<JsonApiError> = emptyList(),
    var rawMeta: JSONObject? = null,
) {
    companion object {
        const val MEDIA_TYPE = "application/vnd.api+json"
        const val JSON_DATA = "data"
        const val JSON_DATA_TYPE = "type"
        const val JSON_DATA_ID = "id"
        const val JSON_DATA_ATTRIBUTES = "attributes"
        const val JSON_DATA_RELATIONSHIPS = "relationships"
        const val JSON_ERRORS = "errors"
        const val JSON_INCLUDED = "included"
        const val JSON_META = "meta"

        @JvmStatic
        fun <T : Any> single(data: T?) = JsonApiObject(true, data = listOfNotNull(data))

        @JvmStatic
        @SafeVarargs
        fun <T : Any> of(vararg data: T) = JsonApiObject(false, data = listOf(*data))

        @JvmStatic
        fun <T : Any> error(vararg errors: JsonApiError) = JsonApiObject<T>(false, errors = listOf(*errors))
    }

    protected constructor(source: JsonApiObject<T>) : this(
        source.isSingle,
        data = source.data,
        errors = source.errors,
        rawMeta = source.rawMeta
    )

    var dataSingle: T?
        get() = data.firstOrNull()
        set(value) {
            data = listOfNotNull(value)
        }

    @get:JvmName("hasErrors")
    val hasErrors get() = errors.isNotEmpty()
}
