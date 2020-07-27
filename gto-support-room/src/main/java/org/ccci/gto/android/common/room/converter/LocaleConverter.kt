package org.ccci.gto.android.common.room.converter

import androidx.room.TypeConverter
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat

@Deprecated("Since v3.4.0, use converter from the gto-support-androidx-room module instead")
object LocaleConverter {
    @JvmStatic
    @TypeConverter
    fun toLocale(tag: String?) = tag?.let { LocaleCompat.forLanguageTag(it) }

    @JvmStatic
    @TypeConverter
    fun toString(locale: Locale?) = locale?.let { LocaleCompat.toLanguageTag(it) }
}
