package org.ccci.gto.android.common.jsonapi.converter

import java.sql.Time
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val DEFAULT = "yyyy-MM-dd'T'HH:mm:ssX"

class DateTypeConverterTest {
    private val converter = DateTypeConverter(DEFAULT)

    @Test(expected = IllegalArgumentException::class)
    fun verifyConstructorInvalidPattern() {
        DateTypeConverter("'")
    }

    @Test
    fun verifySupports() {
        assertFalse(converter.supports(Any::class.java))
        assertTrue(converter.supports(Date::class.java))
        assertFalse(converter.supports(Time::class.java))
    }

    @Test
    fun verifyToString() {
        assertEquals("1970-01-01T00:00:00Z", converter.toString(Date(0)))
    }

    @Test
    fun verifyFromString() {
        assertEquals(Date(0), converter.fromString("1970-01-01T00:00:00Z"))
    }
}
