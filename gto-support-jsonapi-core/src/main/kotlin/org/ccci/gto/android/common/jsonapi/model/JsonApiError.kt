package org.ccci.gto.android.common.jsonapi.model

import org.json.JSONObject

data class JsonApiError(
    val status: Int? = null,
    val code: String? = null,
    val title: String? = null,
    val detail: String? = null,
    val source: Source? = null,
    val rawMeta: JSONObject? = null,
) {
    companion object {
        const val JSON_ERROR_STATUS = "status"
        const val JSON_ERROR_CODE = "code"
        const val JSON_ERROR_TITLE = "title"
        const val JSON_ERROR_DETAIL = "detail"
        const val JSON_ERROR_SOURCE = "source"
        const val JSON_ERROR_META = "meta"
    }

    data class Source(var pointer: String? = null) {
        companion object {
            const val JSON_ERROR_SOURCE_POINTER = "pointer"
        }
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        other == null || this::class != other::class -> false
        other !is JsonApiError -> false
        status != other.status -> false
        code != other.code -> false
        title != other.title -> false
        detail != other.detail -> false
        source != other.source -> false
        rawMeta?.toString() != other.rawMeta?.toString() -> false
        else -> true
    }

    override fun hashCode(): Int {
        var result = status ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (detail?.hashCode() ?: 0)
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + (rawMeta?.toString()?.hashCode() ?: 0)
        return result
    }
}
