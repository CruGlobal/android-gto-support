package org.ccci.gto.android.common.androidx.room.converter

import androidx.room.TypeConverter
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat

object LocaleConverter {
    @JvmStatic
    @TypeConverter
    fun toLocale(tag: String?) = tag?.let { LocaleCompat.forLanguageTag(it) }

    @JvmStatic
    @TypeConverter
    fun toString(locale: Locale?) = locale?.let { LocaleCompat.toLanguageTag(it) }
}
