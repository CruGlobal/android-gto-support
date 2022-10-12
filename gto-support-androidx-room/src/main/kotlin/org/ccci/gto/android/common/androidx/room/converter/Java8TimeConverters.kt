package org.ccci.gto.android.common.androidx.room.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant

object Java8TimeConverters {
    @JvmStatic
    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    internal fun Long?.toInstant() = this?.let { Instant.ofEpochMilli(it) }

    @JvmStatic
    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    internal fun Instant?.toLong() = this?.toEpochMilli()
}
