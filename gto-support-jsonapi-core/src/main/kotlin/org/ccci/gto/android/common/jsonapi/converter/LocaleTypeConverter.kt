package org.ccci.gto.android.common.jsonapi.converter

import java.util.Locale

object LocaleTypeConverter : TypeConverter<Locale> {
    override fun supports(clazz: Class<*>) = Locale::class.java == clazz
    override fun toString(value: Locale?) = value?.toLanguageTag()
    override fun fromString(value: String?) = value?.let { Locale.forLanguageTag(it) }
}
