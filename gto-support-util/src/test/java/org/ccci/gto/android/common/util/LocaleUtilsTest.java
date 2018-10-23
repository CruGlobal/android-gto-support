package org.ccci.gto.android.common.util;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LocaleUtilsTest {
    @Test
    public void verifyGetOptionalDisplayNameExists() {
        assertNotNull(LocaleUtils.getOptionalDisplayName(Locale.ENGLISH, null));
        assertEquals("English", LocaleUtils.getOptionalDisplayName(Locale.ENGLISH, Locale.ENGLISH));
        assertNotNull(LocaleUtils.getOptionalDisplayName(Locale.FRENCH, null));
        assertEquals("français", LocaleUtils.getOptionalDisplayName(Locale.FRENCH, Locale.FRENCH));
    }

    @Test
    public void verifyGetOptionalDisplayNameDoesntExist() {
        assertNull(LocaleUtils.getOptionalDisplayName(new Locale("x"), Locale.ENGLISH));
        assertNull(LocaleUtils.getOptionalDisplayName(new Locale("x"), null));
    }
}
