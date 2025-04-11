package org.ccci.gto.android.common.androidx.room.converter

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Java8TimeConvertersTest {
    @Test
    fun testInstantConverter() = with(Java8TimeConverters) {
        assertNull(toInstant(null))
        assertNull(toLong(null as Instant?))
        assertEquals(Instant.ofEpochMilli(0), toInstant(0L))
        assertEquals(0L, toLong(Instant.ofEpochMilli(0)))
        val instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        assertEquals(instant, toInstant(toLong(instant)))
    }
}
