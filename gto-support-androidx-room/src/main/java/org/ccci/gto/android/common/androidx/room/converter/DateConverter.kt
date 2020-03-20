package org.ccci.gto.android.common.androidx.room.converter

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?) = timestamp?.let { Date(it) }

    @JvmStatic
    @TypeConverter
    fun toLong(date: Date?) = date?.time
}
