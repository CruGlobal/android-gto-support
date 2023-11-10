package org.ccci.gto.android.common.db

import android.os.Parcelable
import androidx.annotation.RestrictTo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Join<S : Any, T : Any> private constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val target: Table<T>,
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val base: Join<S, *>? = null,
    private val type: String? = null,
    private val on: Expression? = null,
) : Parcelable {
    companion object {
        @JvmField
        val NO_JOINS = emptyArray<Join<*, *>>()

        @JvmStatic
        fun <S : Any, T : Any> create(target: Table<T>) = Join<S, T>(target = target)
    }

    internal val allTables: Sequence<Table<*>> get() = sequenceOf(target) + base?.allTables.orEmpty()

    fun type(type: String?) = copy(type = type)
    fun on(on: Expression?) = copy(on = on)
    fun andOn(on: Expression) = copy(on = this.on?.and(on) ?: on)

    fun <T2 : Any> join(target: Class<T2>) = join(Table.forClass(target))
    fun <T2 : Any> join(target: Table<T2>) = Join(target = target, base = this)
    inline fun <reified T2 : Any> join() = join(Table.forClass<T2>())
    fun <T2 : Any> join(join: Join<T, T2>): Join<S, T2> {
        require(join.base == null) { "Cannot join a join that already has a base join" }
        return Join(base = this, target = join.target, type = join.type, on = join.on)
    }

    fun <T2 : Any> leftJoin(join: Join<T, T2>) = join(join).type("LEFT")

    @Transient
    @IgnoredOnParcel
    private var sqlJoin: QueryComponent? = null

    internal fun getSql(dao: AbstractDao): QueryComponent = sqlJoin ?: buildSql(dao).also { sqlJoin = it }

    private fun buildSql(dao: AbstractDao) = base?.getSql(dao) + buildString {
        if (type != null) append(' ').append(type)
        append(" JOIN ").append(target.sqlTable(dao))
    } + on?.buildSql(dao)?.prepend(" ON ")
}
