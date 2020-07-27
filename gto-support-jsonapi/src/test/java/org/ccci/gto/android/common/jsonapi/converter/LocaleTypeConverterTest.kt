package org.ccci.gto.android.common.jsonapi.converter

import java.io.Serializable
import java.util.Locale
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class LocaleTypeConverterTest {
    private val converter = LocaleTypeConverter

    @Test
    fun verifySupports() {
        assertThat(converter.supports(Any::class.java), equalTo(false))
        assertThat(converter.supports(Locale::class.java), equalTo(true))
        assertThat(converter.supports(Cloneable::class.java), equalTo(false))
        assertThat(converter.supports(Serializable::class.java), equalTo(false))
    }

    @Test
    fun verifyToString() {
        assertThat(converter.toString(Locale.ENGLISH), equalTo("en"))
        assertThat(converter.toString(Locale.CANADA), equalTo("en-CA"))
    }

    @Test
    fun verifyFromString() {
        assertThat(converter.fromString("en"), equalTo(Locale.ENGLISH))
        assertThat(converter.fromString("en-CA"), equalTo(Locale.CANADA))
    }

    @Test
    fun verifyNull() {
        assertThat(converter.fromString(null), nullValue())
        assertThat(converter.toString(null), nullValue())
    }
}
