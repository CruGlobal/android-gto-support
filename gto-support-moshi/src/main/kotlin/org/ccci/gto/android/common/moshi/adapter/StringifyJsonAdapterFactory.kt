package org.ccci.gto.android.common.moshi.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Stringify

object StringifyJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? = when {
        annotations.any { it is Stringify } -> StringifiedJsonAdapter<Any>(type, annotations, moshi)
        else -> null
    }
}

internal class StringifiedJsonAdapter<T>(type: Type, annotations: Set<Annotation>, moshi: Moshi) : JsonAdapter<T>() {
    private val adapter = moshi.adapter<T>(type, annotations.filter { it !is Stringify }.toSet())
    private val stringAdapter = moshi.adapter(String::class.java)

    override fun fromJson(reader: JsonReader) = stringAdapter.fromJson(reader)?.let { adapter.fromJson(it) }
    override fun toJson(writer: JsonWriter, value: T?) = stringAdapter.toJson(writer, value?.let { adapter.toJson(it) })
}
