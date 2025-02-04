package org.ccci.gto.android.common.db

import java.util.Locale
import kotlin.random.Random
import org.ccci.gto.android.common.db.model.Model2
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.arrayContaining
import org.hamcrest.beans.HasPropertyWithValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ExpressionTest {
    private val tname = "t${Random.nextInt(0, Int.MAX_VALUE)}"
    private val fname = "f${Random.nextInt(0, Int.MAX_VALUE)}"

    private lateinit var dao: AbstractDao

    private val table = Table.forClass<Model2>()
    private lateinit var field: Expression.Field
    private val expr = Expression.raw("expr")

    @Before
    fun setupField() {
        dao = mock { on { tableName(Model2::class.java) } doReturn tname }
        field = table.field(fname)
    }

    @Test
    fun verifyBindSql() {
        assertThat(Expression.bind().buildSql(dao), matchesQueryComponent("?", ""))
        assertThat(Expression.bind(5).buildSql(dao), matchesQueryComponent("?", "5"))
        assertThat(Expression.bind("test").buildSql(dao), matchesQueryComponent("?", "test"))
        assertThat(Expression.bind(Locale.ENGLISH).buildSql(dao), matchesQueryComponent("?", "en"))
    }

    @Test
    fun verifyConstantSql() {
        assertThat(Expression.NULL.buildSql(dao), matchesQueryComponent("NULL"))
        assertThat(Expression.constant(5).buildSql(dao), matchesQueryComponent("5"))
        assertThat(Expression.constant("test").buildSql(dao), matchesQueryComponent("?", "test"))
        assertThat(Expression.constant(Locale.ENGLISH).buildSql(dao), matchesQueryComponent("?", "en"))
    }

    @Test
    fun verifyFieldSql() {
        assertThat(Expression.field(fname).buildSql(dao), matchesQueryComponent(fname))
    }

    @Test
    fun verifyAndSql() {
        assertThat(
            Expression.constant(1).and(Expression.constant(2)).and(Expression.constant(3)).buildSql(dao),
            matchesQueryComponent("(1 AND 2 AND 3)")
        )
    }

    @Test
    fun verifyEqSql() {
        assertThat(expr.eq(1).buildSql(dao), matchesQueryComponent("(expr == 1)"))
        assertThat(expr.eq("a").buildSql(dao), matchesQueryComponent("(expr == ?)", "a"))
        assertThat(expr.eq(Locale.ENGLISH).buildSql(dao), matchesQueryComponent("(expr == ?)", "en"))
        assertThat(expr.eq(expr).buildSql(dao), matchesQueryComponent("(expr == expr)"))
    }

    @Test
    fun `oneOf()`() {
        assertThat(
            expr.oneOf(Expression.constant(1), Expression.constant(2)).buildSql(dao),
            matchesQueryComponent("(expr IN (1,2))")
        )
    }

    @Test
    fun verifyBinaryArgs() {
        val expression = field.oneOf(Expression.bind(), Expression.bind()).args(1, 2)
        assertThat(expression.buildSql(dao).args, arrayContaining("1", "2"))
    }

    @Test
    fun testNotEqualsSql() {
        assertEquals("($tname.$fname != ?)", field.ne("1").buildSql(dao).sql)
    }

    @Test
    fun testCount() {
        assertEquals("COUNT ($tname.$fname)", field.count().buildSql(dao).sql)
    }

    @Test
    fun testCountInHaving() {
        assertEquals("(COUNT ($tname.$fname) == 1)", field.count().eq(1).buildSql(dao).sql)
    }

    private fun matchesQueryComponent(sql: String = "", vararg args: String) = CoreMatchers.allOf<QueryComponent>(
        HasPropertyWithValue.hasProperty("sql", CoreMatchers.equalTo(sql)),
        HasPropertyWithValue.hasProperty(
            "args",
            if (args.isEmpty()) Matchers.emptyArray() else Matchers.arrayContaining(*args)
        )
    )
}
