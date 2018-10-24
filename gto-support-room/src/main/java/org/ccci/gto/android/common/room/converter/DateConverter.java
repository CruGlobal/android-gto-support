package org.ccci.gto.android.common.room.converter;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class DateConverter {
    @Nullable
    @TypeConverter
    public static Date toDate(@Nullable final Long timestamp) {
        return timestamp != null ? new Date(timestamp) : null;
    }

    @Nullable
    @TypeConverter
    public static Long toLong(@Nullable final Date date) {
        return date != null ? date.getTime() : null;
    }
}
