package org.ccci.gto.android.common.jsonapi.scarlet

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.utils.getParameterUpperBound
import com.tinder.scarlet.utils.getRawType
import com.tinder.scarlet.utils.hasUnresolvableType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import org.ccci.gto.android.common.jsonapi.JsonApiConverter
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.scarlet.stringValue

class JsonApiMessageAdapterFactory(private val jsonApi: JsonApiConverter) : MessageAdapter.Factory {
    override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> =
        when (val raw = type.getRawType()) {
            JsonApiObject::class.java -> {
                require(type is ParameterizedType && !type.hasUnresolvableType()) {
                    "JsonApiObject Message type requires a resolvable ParameterizedType"
                }
                JsonApiObjectMessageAdapter(type.getParameterUpperBound(0).getRawType())
            }
            else -> {
                if (jsonApi.supports(raw)) {
                    ObjectMessageAdapter(raw)
                } else {
                    error("$type is not supported by this MessageAdapter.Factory")
                }
            }
        }

    private inner class JsonApiObjectMessageAdapter<T> internal constructor(private val dataType: Class<T>) :
        MessageAdapter<JsonApiObject<T>> {
        override fun fromMessage(message: Message) = jsonApi.fromJson(message.stringValue, dataType)
        override fun toMessage(data: JsonApiObject<T>) = Message.Text(jsonApi.toJson(data))
    }

    private inner class ObjectMessageAdapter<T> internal constructor(dataType: Class<T>) : MessageAdapter<T> {
        private val wrappedAdapter = JsonApiObjectMessageAdapter(dataType)
        override fun fromMessage(message: Message) = wrappedAdapter.fromMessage(message).apply {
            require(isSingle || data.size == 1) { "Cannot deserialize multiple objects to a single POJO" }
            require(dataSingle != null) { "We don't support deserializing a null POJO" }
        }.dataSingle!!

        override fun toMessage(data: T) = wrappedAdapter.toMessage(JsonApiObject.single(data))
    }
}
