package org.ccci.gto.android.common.jsonapi.converter

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateTypeConverter(private val format: DateFormat) : TypeConverter<Date> {
    constructor(rawFormat: String) : this(SimpleDateFormat(rawFormat, Locale.US)) {
        format.calendar.timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun supports(clazz: Class<*>) = Date::class.java == clazz
    override fun toString(value: Date?) = value?.let { format.format(it) }
    override fun fromString(value: String?) = value?.let {
        try {
            format.parse(it)
        } catch (ignored: ParseException) {
            null
        }
    }
}
