package org.ccci.gto.android.common.moshi.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DateTimeFormat(val value: String)

class JavaTimeJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? = when (type) {
        Instant::class.java -> InstantJsonAdapter(moshi)
        else -> null
    }

    private class InstantJsonAdapter(
        moshi: Moshi,
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT,
    ) : JsonAdapter<Instant>() {
        private val stringAdapter = moshi.adapter(String::class.java)

        override fun fromJson(reader: JsonReader) =
            stringAdapter.fromJson(reader)?.let { formatter.parse(it, Instant::from) }

        override fun toJson(writer: JsonWriter, value: Instant?) =
            stringAdapter.toJson(writer, value?.let { formatter.format(it) })
    }
}
