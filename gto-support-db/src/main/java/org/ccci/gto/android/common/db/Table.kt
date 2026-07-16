package org.ccci.gto.android.common.db

import android.os.Parcelable
import androidx.annotation.RestrictTo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Table<T : Any> internal constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val type: Class<T>,
    private val alias: String? = null,
) : Parcelable {
    companion object {
        @JvmStatic
        fun <T : Any> forClass(type: Class<T>) = Table(type)
        inline fun <reified T : Any> forClass() = forClass(T::class.java)
    }

    @Suppress("ktlint:standard:function-naming")
    fun `as`(alias: String?) = copy(alias = alias)
    fun field(field: String) = Expression.Field(this, field)

    fun <T2 : Any> join(target: Table<T2>) = Join.create<T, T2>(target)
    inline fun <reified T2 : Any> join() = join(forClass(T2::class.java))

    @Transient
    @IgnoredOnParcel
    private var sqlPrefix: String? = null
    @Transient
    @IgnoredOnParcel
    private var sqlTable: String? = null

    internal fun sqlPrefix(dao: AbstractDao) = sqlPrefix ?: "${alias ?: dao.tableName(type)}.".also { sqlPrefix = it }
    internal fun sqlTable(dao: AbstractDao) = sqlTable ?: buildString {
        append(dao.tableName(type))
        if (alias != null) append(" AS ").append(alias)
    }.also { sqlTable = it }
}
