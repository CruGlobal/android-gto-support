package org.ccci.gto.android.common.jsonapi.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IncludesTest {
    @Test
    fun verifyIncludeAll() {
        val includes = Includes(null)
        assertTrue(includes.include("ajslkdf"))
        assertTrue(includes.include("whe"))
    }

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
    fun verifyDescendantIncludeAll() {
        val includes = Includes(null)
        assertTrue(includes.include("ajslkdf"))
        assertTrue(includes.include("whe"))
        assertTrue(includes.descendant("akjsdflj").include("h5h"))
    }

    @Test
    fun verifyDescendantImplict() {
        val includes = Includes("a.b.c", "de.f.g")
        assertTrue(includes.include("a"))
        assertTrue(includes.descendant("a").include("b"))
        assertTrue(includes.descendant("a").descendant("b").include("c"))
        assertFalse(includes.descendant("a").descendant("h").include("c"))
    }

    @Test
    fun verifyMerge() {
        val includes = Includes("a.b.c").merge(Includes("de.f.g"))
        assertTrue(includes.include("a"))
        assertTrue(includes.descendant("a").include("b"))
        assertTrue(includes.descendant("a").descendant("b").include("c"))
        assertTrue(includes.include("de"))
        assertTrue(includes.descendant("de").include("f"))
        assertTrue(includes.descendant("de").descendant("f").include("g"))
        assertFalse(includes.descendant("a").descendant("h").include("c"))
    }

    @Test
    fun verifyMergeNull() {
        val includes = Includes("a.b.c", "de.f.g").merge(null)
        assertTrue(includes.include("a"))
        assertTrue(includes.descendant("a").include("b"))
        assertTrue(includes.descendant("a").descendant("b").include("c"))
        assertFalse(includes.descendant("a").descendant("h").include("c"))
    }

    @Test
    fun testMergeIncludeAll() {
        val includes1: Includes = Includes(null).merge(Includes())
        assertTrue(includes1.include("ajslkdf"))
        assertTrue(includes1.include("whe"))
        assertTrue(includes1.descendant("akjsdflj").include("h5h"))

        val includes2 = Includes().merge(Includes(null))
        assertTrue(includes2.include("ajslkdf"))
        assertTrue(includes2.include("whe"))
        assertTrue(includes2.descendant("akjsdflj").include("h5h"))
    }
}
