package org.ccci.gto.android.common.jsonapi.retrofit2.model

import org.ccci.gto.android.common.jsonapi.JsonApiConverter
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject

class JsonApiRetrofitObject<T : Any>(obj: JsonApiObject<T>) : JsonApiObject<T>(obj) {
    companion object {
        fun <T : Any> single(data: T?) = JsonApiRetrofitObject(JsonApiObject.single(data))
    }

    var options: JsonApiConverter.Options? = null
}
