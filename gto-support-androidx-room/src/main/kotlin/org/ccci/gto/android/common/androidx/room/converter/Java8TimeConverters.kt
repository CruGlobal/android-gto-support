package org.ccci.gto.android.common.androidx.room.converter

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.Instant

@SuppressLint("NewApi")
object Java8TimeConverters {
    @JvmStatic
    @TypeConverter
    fun toInstant(epochMillis: Long?) = epochMillis?.let { Instant.ofEpochMilli(it) }

    @JvmStatic
    @TypeConverter
    fun toLong(instant: Instant?) = instant?.toEpochMilli()
}
