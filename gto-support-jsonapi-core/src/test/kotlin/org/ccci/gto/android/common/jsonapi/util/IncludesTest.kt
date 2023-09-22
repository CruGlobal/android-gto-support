package org.ccci.gto.android.common.jsonapi.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IncludesTest {
    @Test
    fun verifyIncludeSimple() {
        val includes = Includes("a", "b")
        assertTrue(includes.include("a"))
        assertTrue(includes.include("b"))
        assertFalse(includes.include("c"))
    }

    @Test
    fun verifyIncludeImplicit() {
        val includes = Includes("a.b", "bc", "d.e")
        assertTrue(includes.include("a"))
        assertFalse(includes.include("b"))
        assertTrue(includes.include("d"))
    }

    @Test
    fun verifyDescendantImplicit() {
        val includes = Includes("a.b.c", "de.f.g")
        assertTrue(includes.include("a"))
        assertTrue(includes.descendant("a").include("b"))
        assertTrue(includes.descendant("a").descendant("b").include("c"))
        assertFalse(includes.descendant("a").descendant("h").include("c"))
    }

    @Test
    fun `merge()`() {
        val includes = Includes.merge(Includes("a.b.c"), Includes("de.f.g"))!!
        assertTrue(includes.include("a"))
        assertTrue(includes.descendant("a").include("b"))
        assertTrue(includes.descendant("a").descendant("b").include("c"))
        assertTrue(includes.include("de"))
        assertTrue(includes.descendant("de").include("f"))
        assertTrue(includes.descendant("de").descendant("f").include("g"))
        assertFalse(includes.descendant("a").descendant("h").include("c"))
    }
}
