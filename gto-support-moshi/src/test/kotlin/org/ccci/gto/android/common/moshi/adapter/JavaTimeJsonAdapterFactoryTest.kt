package org.ccci.gto.android.common.moshi.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import java.time.Instant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalStdlibApi::class)
class JavaTimeJsonAdapterFactoryTest {
    private val moshi = Moshi.Builder()
        .add(JavaTimeJsonAdapterFactory())
        .build()

    @Test
    fun testInstant_fromJson_parsesIsoInstant() {
        val adapter = moshi.adapter<Instant>()
        val json = "\"2020-01-01T12:34:56Z\""

        val instant = adapter.fromJson(json)
        assertEquals(Instant.ofEpochSecond(1577882096), instant)
    }

    @Test
    fun testInstant_toJson_writesIsoInstant() {
        val adapter = moshi.adapter<Instant>()
        val instant = Instant.ofEpochSecond(1577882096)

        val json = adapter.toJson(instant)
        assertEquals("\"2020-01-01T12:34:56Z\"", json)
    }

    @Test
    fun testInstant_nullHandling() {
        val adapter = moshi.adapter<Instant?>()

        // toJson with null should produce JSON null
        val written = adapter.toJson(null)
        assertEquals("null", written)

        // fromJson with JSON null should return null
        val read = adapter.fromJson("null")
        assertNull(read)
    }

    @Test
    fun testInstant_roundTrip_withMoshiAdapter() {
        val adapter = moshi.adapter<Instant>()
        val original = Instant.ofEpochSecond(Random.nextLong(Instant.MIN.epochSecond, Instant.MAX.epochSecond))

        val json = adapter.toJson(original)
        val parsed = adapter.fromJson(json)

        assertEquals(original, parsed)
    }
}
