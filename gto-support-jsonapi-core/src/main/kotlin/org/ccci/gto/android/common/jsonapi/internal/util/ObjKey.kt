package org.ccci.gto.android.common.jsonapi.internal.util

import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.json.JSONObject

internal data class ObjKey(val type: String, val id: String) {
    companion object {
        @JvmStatic
        @JvmName("create")
        internal fun JSONObject.toObjKey(): ObjKey? {
            val type = optString(JsonApiObject.JSON_DATA_TYPE, null) ?: return null
            val id = optString(JsonApiObject.JSON_DATA_ID, null) ?: return null

            return ObjKey(type, id)
        }
    }
}
