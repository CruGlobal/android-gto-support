package org.ccci.gto.android.common.androidx.room.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
object Java8TimeConverters {
    @JvmStatic
    @TypeConverter
    internal fun Long?.toInstant() = this?.let { Instant.ofEpochMilli(it) }

    @JvmStatic
    @TypeConverter
    internal fun Instant?.toLong() = this?.toEpochMilli()
}
