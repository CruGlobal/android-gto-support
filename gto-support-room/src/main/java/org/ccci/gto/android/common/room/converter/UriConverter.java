package org.ccci.gto.android.common.room.converter;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;
import android.support.annotation.Nullable;

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
