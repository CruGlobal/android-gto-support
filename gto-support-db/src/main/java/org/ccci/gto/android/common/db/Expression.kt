package org.ccci.gto.android.common.db

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.ccci.gto.android.common.db.AbstractDao.Companion.bindValues

sealed class Expression : Parcelable {
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
        fun not(expression: Expression) = expression.not()

        fun raw(expr: String): Expression = Raw(expr, emptyList())
        fun raw(expr: String, vararg args: Any): Expression = Raw(expr, bindValues(*args).toList())
        fun raw(expr: String, vararg args: String): Expression = Raw(expr, args.toList())
    }

    /**
     * The number of "dynamic" arguments in this expression. This may not be the same as the actual number of arguments
     * returned from buildSql
     */
    internal open val numOfArgs = 0

    fun args(vararg args: Any) = args(*bindValues(*args))
    open fun args(vararg args: String): Expression {
        require(args.isEmpty()) { "invalid number of arguments specified" }
        return this
    }

    infix fun and(expression: Expression): Expression = binaryExpr(Binary.AND, expression)
    infix fun or(expression: Expression): Expression = binaryExpr(Binary.OR, expression)
    open operator fun not(): Expression = Unary(Unary.NOT, this)

    infix fun eq(constant: Number): Expression = eq(constant(constant))
    infix fun eq(constant: String): Expression = eq(constant(constant))
    infix fun eq(constant: Any): Expression = eq(constant(constant))
    infix fun eq(expression: Expression): Expression = Binary(Binary.EQ, this, expression)

    infix fun lt(constant: Number): Expression = lt(constant(constant))
    infix fun lt(constant: Any): Expression = lt(constant(constant))
    infix fun lt(expression: Expression): Expression = Binary(Binary.LT, this, expression)

    infix fun lte(constant: Number): Expression = lte(constant(constant))
    infix fun lte(constant: Any): Expression = lte(constant(constant))
    infix fun lte(expression: Expression): Expression = Binary(Binary.LTE, this, expression)

    infix fun gt(constant: Number): Expression = gt(constant(constant))
    infix fun gt(constant: Any): Expression = gt(constant(constant))
    infix fun gt(expression: Expression): Expression = Binary(Binary.GT, this, expression)

    infix fun gte(constant: Number): Expression = gte(constant(constant))
    infix fun gte(constant: Any): Expression = gte(constant(constant))
    infix fun gte(expression: Expression): Expression = Binary(Binary.GTE, this, expression)

    fun oneOf(vararg expressions: Expression): Expression = Binary(Binary.IN, this, *expressions)
    fun oneOf(literals: List<Expression>): Expression = Binary(Binary.IN, this, *literals.toTypedArray())
    fun notIn(vararg expressions: Expression): Expression = Binary(Binary.NOTIN, this, *expressions)

    @Suppress("ktlint:standard:function-naming")
    fun `is`(expression: Expression): Expression = Binary(Binary.IS, this, expression)
    fun isNull(): Expression = Binary(Binary.IS, this, NULL)
    fun isNot(expression: Expression): Expression = Binary(Binary.ISNOT, this, expression)

    infix fun ne(constant: Number): Expression = ne(constant(constant))
    infix fun ne(constant: String): Expression = ne(constant(constant))
    infix fun ne(constant: Any): Expression = ne(constant(constant))
    infix fun ne(expression: Expression): Expression = Binary(Binary.NE, this, expression)

    internal open fun binaryExpr(op: String, expression: Expression): Expression = Binary(op, this, expression)

    internal abstract fun buildSql(dao: AbstractDao): QueryComponent

    @Parcelize
    data class Field internal constructor(
        private val table: Table<*>? = null,
        private val name: String,
    ) : Expression() {
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
        private var sql: QueryComponent? = null

        override fun buildSql(dao: AbstractDao) =
            sql ?: QueryComponent(if (table != null) "${table.sqlPrefix(dao)}$name" else name).also { sql = it }
    }

    @Parcelize
    data class Literal internal constructor(
        private val strValue: String? = null,
        private val numValue: Number? = null,
        private val isConstant: Boolean,
    ) : Expression() {
        internal constructor(value: Number, constant: Boolean) : this(null, value, constant)
        internal constructor(value: String, constant: Boolean) : this(value, null, constant)

        override val numOfArgs get() = if (isConstant) 0 else 1

        override fun args(vararg args: String): Literal {
            require(args.size == numOfArgs) { "incorrect number of args specified" }
            return if (isConstant) this else Literal(args[0], false)
        }

        @IgnoredOnParcel
        private val sql by lazy {
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

        override fun buildSql(dao: AbstractDao) = sql
    }

    @Parcelize
    private data class Raw(private val expr: String, private val args: List<String>) : Expression() {
        override val numOfArgs get() = args.size

        override fun args(vararg args: String) = copy(args = args.toList())

        override fun buildSql(dao: AbstractDao) = QueryComponent(expr, *args.toTypedArray())
    }

    @Parcelize
    private data class Binary(private val op: String, private val exprs: List<Expression>) : Expression() {
        companion object {
            internal const val LT = "<"
            internal const val LTE = "<="
            internal const val GT = ">"
            internal const val GTE = ">="
            internal const val EQ = "=="
            internal const val NE = "!="
            internal const val IS = "IS"
            internal const val ISNOT = "IS NOT"
            internal const val IN = "IN"
            internal const val NOTIN = "NOT IN"
            internal const val AND = "AND"
            internal const val OR = "OR"
        }

        constructor(op: String, vararg exprs: Expression) : this(op, exprs.toList())

        @Transient
        @IgnoredOnParcel
        override val numOfArgs = exprs.sumOf { it.numOfArgs }

        override fun args(vararg args: String): Expression {
            require(args.size == numOfArgs) { "incorrect number of args specified" }
            if (args.isEmpty()) return this

            var pos = 0
            val exprs = exprs.map {
                when (it.numOfArgs) {
                    0 -> it
                    else -> it.args(*args.sliceArray(pos until pos + it.numOfArgs)).also { pos += it.numOfArgs }
                }
            }

            return Binary(op, *exprs.toTypedArray())
        }

        override fun not() = when (op) {
            EQ -> Binary(NE, *exprs.toTypedArray())
            NE -> Binary(EQ, *exprs.toTypedArray())
            IS -> Binary(ISNOT, *exprs.toTypedArray())
            ISNOT -> Binary(IS, *exprs.toTypedArray())
            IN -> Binary(NOTIN, *exprs.toTypedArray())
            NOTIN -> Binary(IN, *exprs.toTypedArray())
            else -> super.not()
        }

        override fun binaryExpr(op: String, expression: Expression) = when {
            this.op != op -> super.binaryExpr(op, expression)

            // chain binary expressions together when possible
            op == AND || op == OR -> Binary(op, *exprs.toTypedArray(), expression)

            else -> super.binaryExpr(op, expression)
        }

        @Transient
        @IgnoredOnParcel
        private var sql: QueryComponent? = null
        private fun generateSql(dao: AbstractDao): QueryComponent {
            val query = QueryComponent("(") + when (op) {
                IN, NOTIN -> {
                    exprs[0].buildSql(dao) + " $op (" + exprs.subList(1, exprs.size)
                        .joinToQueryComponent(",") { it.buildSql(dao) } + ")"
                }

                else -> exprs.joinToQueryComponent(" $op ") { it.buildSql(dao) }
            } + ")"
            sql = query
            return query
        }

        override fun buildSql(dao: AbstractDao) = sql ?: generateSql(dao)
    }

    @Parcelize
    private data class Unary(private val op: String, private val expr: Expression) : Expression() {
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
        private var sql: QueryComponent? = null
        private fun generateSql(dao: AbstractDao) =
            (QueryComponent("$op (") + expr.buildSql(dao) + ")").also { sql = it }

        override fun buildSql(dao: AbstractDao) = sql ?: generateSql(dao)
    }

    @Parcelize
    data class Aggregate internal constructor(
        private val op: String,
        private val isDistinct: Boolean,
        private val expr: Field,
    ) : Expression() {
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
        private var sql: QueryComponent? = null

        // {mOp} (DISTINCT {expr})
        private fun generateSql(dao: AbstractDao) =
            (QueryComponent("$op (${if (isDistinct) "DISTINCT " else ""}") + expr.buildSql(dao) + ")")
                .also { sql = it }

        override fun buildSql(dao: AbstractDao) = sql ?: generateSql(dao)
    }
}
