package org.ccci.gto.android.common.jsonapi.converter

import java.time.Instant
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val ISO_EPOCH = "1970-01-01T00:00:00Z"

class InstantConverterTest {
    private val converter = InstantConverter()

    @Test
    fun verifySupports() {
        assertFalse(converter.supports(Any::class.java))
        assertFalse(converter.supports(Date::class.java))
        assertTrue(converter.supports(Instant::class.java))
    }

    @Test
    fun verifyToString() {
        assertEquals(ISO_EPOCH, converter.toString(Instant.ofEpochMilli(0)))
    }

    @Test
    fun verifyFromString() {
        assertEquals(Instant.ofEpochMilli(0), converter.fromString(ISO_EPOCH))
    }
}
