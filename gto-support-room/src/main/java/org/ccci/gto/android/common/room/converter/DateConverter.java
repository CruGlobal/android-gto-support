package org.ccci.gto.android.common.room.converter;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import java.util.Date;

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
