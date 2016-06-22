package org.ccci.gto.android.common.jsonapi.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTypeConverter implements TypeConverter<Date> {
    @NonNull
    private final DateFormat mFormat;

    public DateTypeConverter(@NonNull final String format) {
        this(new SimpleDateFormat(format, Locale.US));
        mFormat.getCalendar().setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public DateTypeConverter(@NonNull final DateFormat format) {
        mFormat = format;
    }

    @Override
    public boolean supports(@NonNull final Class<?> clazz) {
        return Date.class.equals(clazz);
    }

    @Nullable
    @Override
    public Date fromString(@Nullable final String value) {
        if (value != null) {
            try {
                return mFormat.parse(value);
            } catch (final ParseException ignored) {
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable final Date value) {
        return value != null ? mFormat.format(value) : null;
    }
}
