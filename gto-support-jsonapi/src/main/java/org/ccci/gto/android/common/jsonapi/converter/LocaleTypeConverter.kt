package org.ccci.gto.android.common.jsonapi.converter

import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat.forLanguageTag
import org.ccci.gto.android.common.compat.util.LocaleCompat.toLanguageTag

object LocaleTypeConverter : TypeConverter<Locale> {
    override fun supports(clazz: Class<*>) = Locale::class.java == clazz
    override fun toString(value: Locale?) = value?.let { toLanguageTag(it) }
    override fun fromString(value: String?) = value?.let { forLanguageTag(it) }
}
