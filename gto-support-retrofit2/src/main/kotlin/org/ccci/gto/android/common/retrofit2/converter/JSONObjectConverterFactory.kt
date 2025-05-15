package org.ccci.gto.android.common.retrofit2.converter

import java.io.IOException
import java.lang.reflect.Type
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit

object JSONObjectConverterFactory : Converter.Factory() {
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<*, RequestBody>? = when (type) {
        JSONObject::class.java, JSONArray::class.java -> JSONObjectRequestBodyConverter
        else -> null
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit) = when (type) {
        JSONObject::class.java -> JSONObjectResponseBodyConverter
        JSONArray::class.java -> JSONArrayResponseBodyConverter
        else -> null
    }

    private object JSONObjectRequestBodyConverter : Converter<Any?, RequestBody> {
        private val MEDIA_TYPE = "text/json; charset=UTF-8".toMediaType()

        override fun convert(value: Any?): RequestBody = RequestBody.create(MEDIA_TYPE, value?.toString().orEmpty())
    }

    private object JSONObjectResponseBodyConverter : Converter<ResponseBody, JSONObject> {
        override fun convert(value: ResponseBody) = try {
            JSONObject(value.string())
        } catch (e: JSONException) {
            throw IOException("Error parsing JSON", e)
        }
    }

    private object JSONArrayResponseBodyConverter : Converter<ResponseBody, JSONArray> {
        override fun convert(value: ResponseBody) = try {
            JSONArray(value.string())
        } catch (e: JSONException) {
            throw IOException("Error parsing JSON", e)
        }
    }
}
