package org.ccci.gto.android.common.jsonapi.retrofit2.model

import org.ccci.gto.android.common.jsonapi.JsonApiConverter
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject

class JsonApiRetrofitObject<T>(obj: JsonApiObject<T>) : JsonApiObject<T>(obj) {
    companion object {
        fun <T> single(data: T?) = JsonApiRetrofitObject(JsonApiObject.single(data))
    }

    var options: JsonApiConverter.Options? = null
}
