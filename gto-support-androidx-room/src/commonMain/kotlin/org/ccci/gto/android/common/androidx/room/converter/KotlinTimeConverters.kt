package org.ccci.gto.android.common.androidx.room.converter

import androidx.room.TypeConverter
import kotlin.time.Instant

object KotlinTimeConverters {
    @TypeConverter
    fun toInstant(epochMillis: Long?) = epochMillis?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun toEpochMillis(instant: Instant?) = instant?.toEpochMilliseconds()
}
