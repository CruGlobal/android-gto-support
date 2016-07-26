package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Includes;
import org.junit.Test;

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
}
