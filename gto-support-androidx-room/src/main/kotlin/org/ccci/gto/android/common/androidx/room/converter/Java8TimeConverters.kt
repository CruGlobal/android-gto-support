package org.ccci.gto.android.common.androidx.room.converter

import androidx.room.TypeConverter
import java.time.Instant

object Java8TimeConverters {
    @JvmStatic
    @TypeConverter
    internal fun Long?.toInstant() = this?.let { Instant.ofEpochMilli(it) }

    @JvmStatic
    @TypeConverter
    internal fun Instant?.toLong() = this?.toEpochMilli()
}
