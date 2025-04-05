package org.ccci.gto.android.common.db

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import org.ccci.gto.android.common.db.AbstractDao.Companion.bindValues

data class Query<T : Any> private constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val table: Table<T>,
    internal val isDistinct: Boolean = false,
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val joins: List<Join<T, *>> = emptyList(),
    internal val projection: List<String>? = null,
    @VisibleForTesting
    internal val where: Expression? = null,
    internal val orderBy: String? = null,
    internal val groupBy: List<Expression.Field> = emptyList(),
    private val having: Expression? = null,
    private val limit: Int? = null,
    private val offset: Int? = null,
) {
    companion object {
        @JvmStatic
        fun <T : Any> select(type: Class<T>) = Query(table = Table.forClass(type))
        @JvmStatic
        fun <T : Any> select(table: Table<T>) = Query(table = table)
        inline fun <reified T : Any> select() = select(T::class.java)
    }

    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val allTables get() = sequenceOf(table) + joins.flatMap { it.allTables }

    fun distinct(isDistinct: Boolean) = copy(isDistinct = isDistinct)
    fun join(vararg joins: Join<T, *>) = copy(joins = this.joins + joins)
    fun joins(vararg joins: Join<T, *>) = copy(joins = joins.toList())
    fun projection(vararg projection: String) = copy(projection = projection.toList().takeUnless { it.isEmpty() })
    fun where(where: Expression?) = copy(where = where)
    fun where(where: String?, vararg args: Any) = where(where, *bindValues(*args))
    fun where(where: String?, vararg args: String) = where(where?.let { Expression.raw(it, args) })
    fun andWhere(expr: Expression) = copy(where = where?.and(expr) ?: expr)
    fun orderBy(orderBy: String?): Query<T> = copy(orderBy = orderBy)
    fun groupBy(vararg groupBy: Expression.Field) = copy(groupBy = groupBy.toList())
    fun having(having: Expression?) = copy(having = having)
    fun limit(limit: Int?) = copy(limit = limit)
    fun offset(offset: Int?) = copy(offset = offset)

    internal fun buildSqlFrom(dao: AbstractDao) =
        QueryComponent(table.sqlTable(dao)) + joins.joinToQueryComponent { it.getSql(dao) }
    internal fun buildSqlWhere(dao: AbstractDao) = where?.buildSql(dao)
    internal fun buildSqlHaving(dao: AbstractDao) = having?.buildSql(dao)

    internal val sqlLimit get() = when {
        // // XXX: not supported by Android
        // // "{limit} OFFSET {offset}" syntax
        // limit != null && offset != null -> "$limit OFFSET $offset"
        // "{offset},{limit}" syntax
        limit != null && offset != null -> "$offset, $limit"
        limit != null -> "$limit"
        else -> null
    }
}
