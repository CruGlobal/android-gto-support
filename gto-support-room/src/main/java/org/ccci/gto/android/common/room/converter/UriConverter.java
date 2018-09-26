package org.ccci.gto.android.common.room.converter;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class UriConverter {
    @Nullable
    @TypeConverter
    public static Uri toUri(@Nullable final String uri) {
        return uri != null ? Uri.parse(uri) : null;
    }

    @Nullable
    @TypeConverter
    public static String toString(@Nullable final Uri uri) {
        return uri != null ? uri.toString() : null;
    }
}
