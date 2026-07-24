package org.ccci.gto.android.common.androidx.room.converter

import androidx.room.TypeConverter
import java.util.Locale

object LocaleConverter {
    @JvmStatic
    @TypeConverter
    fun toLocale(tag: String?) = tag?.let { Locale.forLanguageTag(it) }

    @JvmStatic
    @TypeConverter
    fun toString(locale: Locale?) = locale?.toLanguageTag()
}
