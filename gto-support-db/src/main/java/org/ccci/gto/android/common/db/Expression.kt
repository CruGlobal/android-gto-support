package org.ccci.gto.android.common.db

import android.os.Parcelable
import android.util.Pair
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.ccci.gto.android.common.db.AbstractDao.Companion.bindValues
import org.ccci.gto.android.common.db.Expression.Aggregate

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

        @JvmStatic
        fun not(expression: KotlinExpression) = expression.not()
    }

    /**
     * The number of "dynamic" arguments in this expression. This may not be the same as the actual number of arguments
     * returned from buildSql
     */
    protected open val numOfArgs = 0

    open fun args(vararg args: Any) = args(*bindValues(*args))
    open fun args(vararg args: String): KotlinExpression {
        require(args.isEmpty()) { "invalid number of arguments specified" }
        return this
    }

    open operator fun not(): KotlinExpression = Unary(Unary.NOT, this)

    fun raw(expr: String, vararg args: Any) = Raw(expr, bindValues(*args).toList())
    fun raw(expr: String, vararg args: String) = Raw(expr, args.toList())

    open fun toRaw(dao: AbstractDao) = buildSql(dao).let { raw(it.sql, *it.args) }

    // TODO: make internal
    internal abstract fun buildSql(dao: AbstractDao): QueryComponent

    @Parcelize
    data class Field internal constructor(
        private val table: Table<*>? = null,
        private val name: String
    ) : KotlinExpression() {
        @JvmOverloads
        fun count(isDistinct: Boolean = false) = Aggregate(Aggregate.COUNT, isDistinct, this)
        @JvmOverloads
        fun max(isDistinct: Boolean = false) = Aggregate(Aggregate.MAX, isDistinct, this)
        @JvmOverloads
        fun min(isDistinct: Boolean = false) = Aggregate(Aggregate.MIN, isDistinct, this)
        @JvmOverloads
        fun sum(isDistinct: Boolean = false) = Aggregate(Aggregate.SUM, isDistinct, this)

        @Transient
        @IgnoredOnParcel
        private var _sql: QueryComponent? = null

        override fun buildSql(dao: AbstractDao) =
            _sql ?: QueryComponent(if (table != null) "${table.sqlPrefix(dao)}$name" else name).also { _sql = it }
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

        override fun buildSql(dao: AbstractDao) = _sql
    }

    @Parcelize
    data class Raw internal constructor(private val expr: String, private val args: List<String>) : KotlinExpression() {
        override val numOfArgs get() = args.size

        override fun args(vararg args: String) = copy(args = args.toList())

        override fun toRaw(dao: AbstractDao) = this

        override fun buildSql(dao: AbstractDao) = QueryComponent(expr, *args.toTypedArray())
    }

    @Parcelize
    internal data class Unary internal constructor(
        private val op: String,
        private val expr: KotlinExpression
    ) : KotlinExpression() {
        companion object {
            internal const val NOT = "NOT"
        }

        override val numOfArgs get() = expr.numOfArgs

        override fun args(vararg args: String) = copy(expr = expr.args(*args))
        override fun not() = when (op) {
            NOT -> expr
            else -> super.not()
        }

        @Transient
        @IgnoredOnParcel
        private var _sql: QueryComponent? = null
        private fun generateSql(dao: AbstractDao) =
            (QueryComponent("$op (") + expr.buildSql(dao) + ")").also { _sql = it }

        override fun buildSql(dao: AbstractDao) = _sql ?: generateSql(dao)
    }

    @Parcelize
    data class Aggregate internal constructor(
        private val op: String,
        private val isDistinct: Boolean,
        private val expr: Field
    ) : KotlinExpression() {
        companion object {
            internal const val COUNT = "COUNT"
            internal const val MAX = "MAX"
            internal const val MIN = "MIN"
            internal const val SUM = "SUM"
        }

        override val numOfArgs get() = expr.numOfArgs

        override fun args(vararg args: String) = copy(expr = expr.args(*args) as Field)

        fun distinct(isDistinct: Boolean) = copy(isDistinct = isDistinct)

        @Transient
        @IgnoredOnParcel
        private var _sql: QueryComponent? = null
        private fun generateSql(dao: AbstractDao) =
            // {mOp} (DISTINCT {expr})
            (QueryComponent("$op (${if (isDistinct) "DISTINCT " else ""}") + expr.buildSql(dao) + ")")
                .also { _sql = it }

        override fun buildSql(dao: AbstractDao) = _sql ?: generateSql(dao)
    }
}

abstract class ShimExpression : KotlinExpression()
