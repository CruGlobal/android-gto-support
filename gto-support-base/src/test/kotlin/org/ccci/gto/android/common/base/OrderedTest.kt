package org.ccci.gto.android.common.base

import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.android.common.base.Ordered.Companion.HIGHEST_PRECEDENCE
import org.ccci.gto.android.common.base.Ordered.Companion.LOWEST_PRECEDENCE

class OrderedTest {
    private val default = object : Ordered {}
    private val highest = object : Ordered {
        override val order = HIGHEST_PRECEDENCE
    }
    private val lowest = object : Ordered {
        override val order = LOWEST_PRECEDENCE
    }

    @Test
    fun `COMPARATOR - should return ordered list`() {
        assertEquals(
            listOf(highest, default, lowest),
            listOf(default, highest, lowest).shuffled().sortedWith(Ordered.COMPARATOR),
        )
    }

    @Test
    fun `COMPARATOR - items should have default precedence if they don't implement Ordered`() {
        assertEquals(0, Ordered.COMPARATOR.compare("string", default))
        assertEquals(0, Ordered.COMPARATOR.compare("string", "other"))
        assertEquals(
            listOf(highest, "string", lowest),
            listOf(highest, "string", lowest).shuffled().sortedWith(Ordered.COMPARATOR),
        )
    }
}
