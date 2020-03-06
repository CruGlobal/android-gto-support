package org.ccci.gto.android.common.room.converter

import androidx.room.TypeConverter
import org.ccci.gto.android.common.compat.util.LocaleCompat
import java.util.Locale

object LocaleConverter {
    @JvmStatic
    @TypeConverter
    fun toLocale(tag: String?) = tag?.let { LocaleCompat.forLanguageTag(it) }

    @JvmStatic
    @TypeConverter
    fun toString(locale: Locale?) = locale?.let { LocaleCompat.toLanguageTag(it) }
}
