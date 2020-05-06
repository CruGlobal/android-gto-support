package org.ccci.gto.android.common.jsonapi.converter

interface TypeConverter<T> {
    fun supports(clazz: Class<*>): Boolean
    fun toString(value: T?): String?
    fun fromString(value: String?): T?
}
