package org.ccci.gto.android.common.room.converter

import androidx.room.TypeConverter
import java.util.Date

@Deprecated("Since v3.4.0, use converter from the gto-support-androidx-room module instead")
object DateConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?) = timestamp?.let { Date(it) }

    @JvmStatic
    @TypeConverter
    fun toLong(date: Date?) = date?.time
}
