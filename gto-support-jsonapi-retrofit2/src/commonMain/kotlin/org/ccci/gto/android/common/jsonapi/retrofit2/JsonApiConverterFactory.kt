package org.ccci.gto.android.common.jsonapi.retrofit2

import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.ccci.gto.android.common.jsonapi.JsonApiConverter
import org.ccci.gto.android.common.jsonapi.internal.util.ReflectionUtils
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude
import org.ccci.gto.android.common.jsonapi.retrofit2.model.JsonApiRetrofitObject
import org.ccci.gto.android.common.jsonapi.util.Includes
import org.json.JSONException
import retrofit2.Converter
import retrofit2.Retrofit

private val MEDIA_TYPE = MediaType.parse(JsonApiObject.MEDIA_TYPE)

class JsonApiConverterFactory(private val converter: JsonApiConverter) : Converter.Factory() {
    constructor(vararg classes: Class<*>) : this(JsonApiConverter.Builder().addClasses(*classes).build())

    companion object {
        @Deprecated(
            "Since v4.0.1, use constructor instead.",
            ReplaceWith(
                "JsonApiConverterFactory(converter)",
                "org.ccci.gto.android.common.jsonapi.retrofit2.JsonApiConverterFactory",
            ),
        )
        fun create(converter: JsonApiConverter) = JsonApiConverterFactory(converter)

        @Deprecated(
            "Since v4.0.1, use constructor instead.",
            ReplaceWith(
                "JsonApiConverterFactory(*classes)",
                "org.ccci.gto.android.common.jsonapi.retrofit2.JsonApiConverterFactory",
            ),
        )
        fun create(vararg classes: Class<*>) = JsonApiConverterFactory(*classes)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<*, RequestBody>? {
        val rawType = ReflectionUtils.getRawType(type)
        val include = parameterAnnotations.filterIsInstance<JsonApiInclude>().firstOrNull()
        return when {
            JsonApiObject::class.java.isAssignableFrom(rawType) -> {
                require(type is ParameterizedType) { "JsonApiObject needs to be parameterized" }
                JsonApiObjectRequestBodyConverter(include)
            }
            Collection::class.java.isAssignableFrom(rawType) && type is ParameterizedType &&
                converter.supports(getRawType(type.actualTypeArguments[0])) -> CollectionRequestBodyConverter(include)
            converter.supports(rawType) -> ObjectRequestBodyConverter(include)
            else -> null
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        val rawType = ReflectionUtils.getRawType(type)
        return when {
            JsonApiObject::class.java.isAssignableFrom(rawType) -> {
                require(type is ParameterizedType) { "JsonApiObject needs to be parameterized" }
                return JsonApiObjectResponseBodyConverter(ReflectionUtils.getRawType(type.actualTypeArguments[0]))
            }
            Collection::class.java.isAssignableFrom(rawType) -> null // TODO
            converter.supports(rawType) -> ObjectResponseBodyConverter(rawType)
            else -> null
        }
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<*, String>? = when (type) {
        Includes::class.java -> JsonApiIncludesStringConverter
        else -> null
    }

    private inner class JsonApiObjectRequestBodyConverter(
        include: JsonApiInclude?,
    ) : Converter<JsonApiObject<*>, RequestBody> {
        private val options = JsonApiConverter.Options.builder()
            .apply {
                when {
                    include == null -> Unit
                    include.all -> includeAll()
                    else -> include(*include.value)
                }
            }
            .build()

        override fun convert(value: JsonApiObject<*>): RequestBody {
            val options = if (value is JsonApiRetrofitObject<*>) options.merge(value.options) else options
            return RequestBody.create(MEDIA_TYPE, converter.toJson(value, options).toByteArray(Charsets.UTF_8))
        }
    }

    private inner class JsonApiObjectResponseBodyConverter<T : Any>(
        private val type: Class<T>,
    ) : Converter<ResponseBody, JsonApiObject<T>> {
        override fun convert(value: ResponseBody) = try {
            converter.fromJson(value.string(), type)
        } catch (e: JSONException) {
            throw IOException("Error parsing JSON", e)
        }
    }

    private inner class CollectionRequestBodyConverter(
        include: JsonApiInclude?
    ) : Converter<Collection<Any>, RequestBody> {
        private val wrappedConverter = JsonApiObjectRequestBodyConverter(include)
        override fun convert(value: Collection<Any>) =
            wrappedConverter.convert(JsonApiObject.of(*value.toTypedArray()))
    }

    private inner class ObjectRequestBodyConverter(include: JsonApiInclude?) : Converter<Any?, RequestBody> {
        private val wrappedConverter = JsonApiObjectRequestBodyConverter(include)
        override fun convert(value: Any?) = wrappedConverter.convert(JsonApiObject.single(value))
    }

    private inner class ObjectResponseBodyConverter<T : Any>(type: Class<T>) : Converter<ResponseBody, T?> {
        private val wrappedConverter = JsonApiObjectResponseBodyConverter(type)
        override fun convert(value: ResponseBody) = wrappedConverter.convert(value).dataSingle
    }

    private object JsonApiIncludesStringConverter : Converter<Includes, String> {
        override fun convert(includes: Includes) = includes.queryParameterValue
    }
}
