package org.ccci.gto.android.common.androidx.room.converter

import java.time.Instant
import java.time.temporal.ChronoUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class Java8TimeConvertersTest {
    @Test
    fun testInstantConverter() = with(Java8TimeConverters) {
        assertNull((null as Long?).toInstant())
        assertNull((null as Instant?).toLong())
        assertEquals(Instant.ofEpochMilli(0), 0L.toInstant())
        assertEquals(0L, Instant.ofEpochMilli(0).toLong())
        val instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        assertEquals(instant, instant.toLong()!!.toInstant())
    }
}
