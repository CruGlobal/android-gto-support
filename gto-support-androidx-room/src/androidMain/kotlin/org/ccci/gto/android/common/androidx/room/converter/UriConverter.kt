package org.ccci.gto.android.common.androidx.room.converter

import android.net.Uri
import androidx.room.TypeConverter

object UriConverter {
    @JvmStatic
    @TypeConverter
    fun toUri(uri: String?) = uri?.let { Uri.parse(uri) }

    @JvmStatic
    @TypeConverter
    fun toString(uri: Uri?) = uri?.toString()
}
