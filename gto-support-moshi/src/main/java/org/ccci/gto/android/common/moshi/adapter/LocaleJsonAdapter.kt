package org.ccci.gto.android.common.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat

object LocaleJsonAdapter {
    @ToJson
    fun toJson(locale: Locale?) = locale?.let { LocaleCompat.toLanguageTag(locale) }

    @FromJson
    fun fromJson(tag: String?) = tag?.let { LocaleCompat.forLanguageTag(tag) }
}
