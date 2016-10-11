package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Includes;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IncludesTest {
    @Test
    public void verifyIncludeAll() throws Exception {
        final Includes includes = new Includes((String[]) null);

        assertTrue(includes.include("ajslkdf"));
        assertTrue(includes.include("whe"));
    }

    @Test
    public void verifyIncludeSimple() throws Exception {
        final Includes includes = new Includes("a", "b");

        assertTrue(includes.include("a"));
        assertTrue(includes.include("b"));
        assertFalse(includes.include("c"));
    }

    @Test
    public void verifyIncludeImplicit() throws Exception {
        final Includes includes = new Includes("a.b", "bc", "d.e");

        assertTrue(includes.include("a"));
        assertFalse(includes.include("b"));
        assertTrue(includes.include("d"));
    }

    @Test
    public void verifyDescendantIncludeAll() {
        final Includes includes = new Includes((String[]) null);

        assertTrue(includes.include("ajslkdf"));
        assertTrue(includes.include("whe"));
        assertTrue(includes.descendant("akjsdflj").include("h5h"));
    }

    @Test
    public void verifyDescendantImplict() throws Exception {
        final Includes includes = new Includes("a.b.c", "de.f.g");

        assertTrue(includes.include("a"));
        assertTrue(includes.descendant("a").include("b"));
        assertTrue(includes.descendant("a").descendant("b").include("c"));
        assertFalse(includes.descendant("a").descendant("h").include("c"));
    }

    @Test
    public void verifyMerge() throws Exception {
        final Includes includes = new Includes("a.b.c").merge(new Includes("de.f.g"));

        assertTrue(includes.include("a"));
        assertTrue(includes.descendant("a").include("b"));
        assertTrue(includes.descendant("a").descendant("b").include("c"));
        assertTrue(includes.include("de"));
        assertTrue(includes.descendant("de").include("f"));
        assertTrue(includes.descendant("de").descendant("f").include("g"));
        assertFalse(includes.descendant("a").descendant("h").include("c"));
    }

    @Test
    public void verifyMergeNull() {
        final Includes includes = new Includes("a.b.c", "de.f.g").merge(null);

        assertTrue(includes.include("a"));
        assertTrue(includes.descendant("a").include("b"));
        assertTrue(includes.descendant("a").descendant("b").include("c"));
        assertFalse(includes.descendant("a").descendant("h").include("c"));
    }

    @Test
    public void testMergeIncludeAll() {
        final Includes includes1 = new Includes((Collection<String>) null).merge(new Includes());
        assertTrue(includes1.include("ajslkdf"));
        assertTrue(includes1.include("whe"));
        assertTrue(includes1.descendant("akjsdflj").include("h5h"));

        final Includes includes2 = new Includes().merge(new Includes((Collection<String>) null));
        assertTrue(includes2.include("ajslkdf"));
        assertTrue(includes2.include("whe"));
        assertTrue(includes2.descendant("akjsdflj").include("h5h"));
    }
}
