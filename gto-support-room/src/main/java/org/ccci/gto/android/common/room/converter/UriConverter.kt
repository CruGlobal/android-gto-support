package org.ccci.gto.android.common.room.converter

import android.net.Uri
import androidx.room.TypeConverter

@Deprecated("Since v3.4.0, use converter from the gto-support-androidx-room module instead")
object UriConverter {
    @JvmStatic
    @TypeConverter
    fun toUri(uri: String?) = uri?.let { Uri.parse(uri) }

    @JvmStatic
    @TypeConverter
    fun toString(uri: Uri?) = uri?.toString()
}
