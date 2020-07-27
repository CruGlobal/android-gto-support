package org.ccci.gto.android.common.jsonapi.converter

import java.sql.Time
import java.util.Date
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

private const val DEFAULT = "yyyy-MM-dd'T'HH:mm:ssX"

class DateTypeConverterTest {
    private val converter = DateTypeConverter(DEFAULT)

    @Test(expected = IllegalArgumentException::class)
    fun verifyConstructorInvalidPattern() {
        DateTypeConverter("'")
    }

    @Test
    fun verifySupports() {
        assertThat(converter.supports(Any::class.java), equalTo(false))
        assertThat(converter.supports(Date::class.java), equalTo(true))
        assertThat(converter.supports(Time::class.java), equalTo(false))
    }

    @Test
    fun verifyToString() {
        assertThat(converter.toString(Date(0)), equalTo("1970-01-01T00:00:00Z"))
    }

    @Test
    fun verifyFromString() {
        assertThat(converter.fromString("1970-01-01T00:00:00Z"), equalTo(Date(0)))
    }
}
