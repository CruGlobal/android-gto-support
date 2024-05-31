package org.ccci.gto.android.common.jsonapi.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.ccci.gto.android.common.jsonapi.JsonApiConverter

class MoshiTypeConverter<T>(private val type: Class<T>, private val adapter: JsonAdapter<T>) : TypeConverter<T> {
    constructor(type: Class<T>, moshi: Moshi) : this(type, moshi.adapter(type))

    override fun supports(clazz: Class<*>) = clazz == type

    override fun fromString(value: String?): T? = value?.let { adapter.fromJson(value) }
    override fun toString(value: T?): String? = value?.let { adapter.toJson(value) }
}

inline fun <reified T> MoshiTypeConverter(adapter: JsonAdapter<T>) = MoshiTypeConverter(T::class.java, adapter)

fun JsonApiConverter.Builder.addMoshiConverters(moshi: Moshi, vararg types: Class<*>) =
    addConverters(types.map { MoshiTypeConverter(it, moshi) })
