package org.ccci.gto.android.common.jsonapi.model

import org.json.JSONObject

class JsonApiError {
    companion object {
        const val JSON_ERROR_STATUS = "status"
        const val JSON_ERROR_TITLE = "title"
        const val JSON_ERROR_DETAIL = "detail"
        const val JSON_ERROR_SOURCE = "source"
        const val JSON_ERROR_META = "meta"
    }

    var status: Int? = null
    var title: String? = null
    var detail: String? = null
    var source: Source? = null
    var rawMeta: JSONObject? = null

    class Source {
        companion object {
            const val JSON_ERROR_SOURCE_POINTER = "pointer"
        }

        var pointer: String? = null
    }
}
