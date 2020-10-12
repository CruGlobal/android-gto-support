package org.ccci.gto.android.common.db

import android.os.Parcelable
import android.util.Pair
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.ccci.gto.android.common.db.AbstractDao.Companion.bindValues
import org.ccci.gto.android.common.db.Expression.Aggregate

private val NO_ARGS = emptyArray<String>()

abstract class KotlinExpression : Parcelable {
    companion object {
        @JvmField
        val NULL = Literal(isConstant = true)

        @JvmStatic
        fun bind() = Literal(isConstant = false)
        @JvmStatic
        fun bind(value: Any) = Literal(bindValues(value)[0], false)
        @JvmStatic
        fun bind(value: Number) = Literal(value, false)
        @JvmStatic
        fun bind(value: String) = Literal(value, false)

        @JvmStatic
        fun constant(value: Any) = Literal(bindValues(value)[0], true)
        @JvmStatic
        fun constant(value: Number) = Literal(value, true)
        @JvmStatic
        fun constant(value: String) = Literal(value, true)

        @JvmStatic
        fun constants(vararg values: Any) = constants(*bindValues(*values))
        @JvmStatic
        fun constants(vararg values: Number) = Array(values.size) { constant(values[it]) }
        @JvmStatic
        fun constants(vararg values: String) = Array(values.size) { constant(values[it]) }

        @JvmStatic
        fun field(name: String) = Field(name = name)
    }

    open fun args(vararg args: Any) = args(*bindValues(*args))

    /**
     * The number of "dynamic" arguments in this expression. This may not be the same as the actual number of arguments
     * returned from buildSql
     */
    protected open val numOfArgs = 0

    open fun args(vararg args: String): KotlinExpression {
        require(args.isEmpty()) { "invalid number of arguments specified" }
        return this
    }

    // TODO: make internal
    abstract fun buildSql(dao: AbstractDao): Pair<String, Array<String>>

    @Parcelize
    data class Field internal constructor(private val table: Table<*>? = null, private val name: String) :
        KotlinExpression() {
        @JvmOverloads
        fun count(distinct: Boolean = false) = Aggregate(Aggregate.COUNT, distinct, this)
        @JvmOverloads
        fun max(distinct: Boolean = false) = Aggregate(Aggregate.MAX, distinct, this)
        @JvmOverloads
        fun min(distinct: Boolean = false) = Aggregate(Aggregate.MIN, distinct, this)
        @JvmOverloads
        fun sum(distinct: Boolean = false) = Aggregate(Aggregate.SUM, distinct, this)

        @Transient
        @IgnoredOnParcel
        private var _sql: QueryComponent? = null

        override fun buildSql(dao: AbstractDao): Pair<String, Array<String>> {
            val query =
                _sql ?: QueryComponent(if (table != null) "${table.sqlPrefix(dao)}$name" else name).also { _sql = it }
            return query.toPair()
        }
    }

    @Parcelize
    data class Literal internal constructor(
        private val strValue: String? = null,
        private val numValue: Number? = null,
        private val isConstant: Boolean
    ) : KotlinExpression() {
        internal constructor(value: Number, constant: Boolean) : this(null, value, constant)
        internal constructor(value: String, constant: Boolean) : this(value, null, constant)

        override val numOfArgs get() = if (isConstant) 0 else 1

        override fun args(vararg args: String): Literal {
            require(args.size == numOfArgs) { "incorrect number of args specified" }
            return if (isConstant) this else Literal(args[0], false)
        }

        @IgnoredOnParcel
        private val _sql by lazy {
            when {
                isConstant -> when {
                    numValue != null -> QueryComponent(numValue.toString())
                    strValue != null -> QueryComponent("?", strValue)
                    else -> QueryComponent("NULL")
                }
                numValue != null -> QueryComponent("?", numValue.toString())
                else -> QueryComponent("?", strValue.orEmpty())
            }
        }

        override fun buildSql(dao: AbstractDao) = _sql.toPair()
    }
}

abstract class ShimExpression : KotlinExpression()
