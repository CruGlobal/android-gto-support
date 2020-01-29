package org.ccci.gto.android.common.db

import android.os.Parcelable
import androidx.annotation.RestrictTo
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Table<T> private constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val type: Class<T>,
    private val alias: String?
) : Parcelable {
    companion object {
        @JvmStatic
        fun <T> forClass(type: Class<T>) = Table(type, null)
    }

    fun `as`(alias: String?) = Table(type, alias)
    fun field(field: String) = Expression.Field(this, field)

    fun <T2> join(target: Table<T2>) = Join.create<T, T2>(target)
    inline fun <reified T2> join() = join(forClass(T2::class.java))

    @Transient
    @IgnoredOnParcel
    private var sqlPrefix: String? = null
    @Transient
    @IgnoredOnParcel
    private var sqlTable: String? = null

    // TODO: make sqlPrefix and sqlTable internal once we convert usages to Kotlin
    fun sqlPrefix(dao: AbstractDao) = sqlPrefix ?: "${alias ?: dao.tableName(type)}.".also { sqlPrefix = it }
    fun sqlTable(dao: AbstractDao) = sqlTable ?: buildString {
        append(dao.tableName(type))
        if (alias != null) append(" AS ").append(alias)
    }.also { sqlTable = it }
}
