package org.ccci.gto.android.common.retrofit2.converter

import java.lang.reflect.Type
import java.util.Locale
import retrofit2.Converter
import retrofit2.Retrofit

object LocaleConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? = when (type) {
        Locale::class.java -> LocaleStringConverter
        else -> null
    }

    private object LocaleStringConverter : Converter<Locale, String> {
        override fun convert(locale: Locale): String? = locale.toLanguageTag()
    }
}
