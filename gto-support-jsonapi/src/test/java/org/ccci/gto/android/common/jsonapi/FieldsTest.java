package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.NonNull;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Fields;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldsTest {
    @Test
    public void verifyFieldsAll() throws Exception {
        final Fields fields = new Fields(null);
        assertTrue(fields.include("asdfjk"));
        assertTrue(fields.include("wije"));
    }

    @Test
    public void verifyFields() throws Exception {
        final Fields fields = fields("a", "b");
        assertTrue(fields.include("a"));
        assertTrue(fields.include("b"));
        assertFalse(fields.include("c"));
    }

    private Fields fields(@NonNull final String... fields) {
        return new Fields(Arrays.asList(fields));
    }
}
