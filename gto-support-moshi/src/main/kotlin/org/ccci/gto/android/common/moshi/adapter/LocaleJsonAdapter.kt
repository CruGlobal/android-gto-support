package org.ccci.gto.android.common.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.Locale

object LocaleJsonAdapter {
    @ToJson
    fun toJson(locale: Locale?) = locale?.toLanguageTag()

    @FromJson
    fun fromJson(tag: String?) = tag?.let { Locale.forLanguageTag(tag) }
}
