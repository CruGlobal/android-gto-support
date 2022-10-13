package org.ccci.gto.android.common.jsonapi.converter

import java.time.Instant
import java.time.format.DateTimeFormatter

class InstantConverter(private val formatter: DateTimeFormatter) : TypeConverter<Instant> {
    constructor() : this(DateTimeFormatter.ISO_INSTANT)

    override fun supports(clazz: Class<*>) = clazz == Instant::class.java
    override fun toString(value: Instant?) = value?.let { formatter.format(it) }
    override fun fromString(value: String?) = value?.let { formatter.parse(it, Instant::from) }
}
