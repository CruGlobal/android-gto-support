package org.ccci.gto.android.common.androidx.room.converter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class KotlinTimeConvertersTest {
    @Test
    fun testInstantConverter() = with(KotlinTimeConverters) {
        assertNull(toInstant(null))
        assertNull(toEpochMillis(null))
        assertEquals(Instant.fromEpochMilliseconds(0), toInstant(0L))
        assertEquals(0L, toEpochMillis(Instant.fromEpochMilliseconds(0)))
        val instant = Instant.fromEpochMilliseconds(1234567890123)
        assertEquals(instant, toInstant(toEpochMillis(instant)))
    }
}
