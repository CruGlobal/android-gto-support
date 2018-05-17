package org.ccci.gto.android.common.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LocaleUtilsIT {
    @Test
    public void testGetFallback() throws Exception {
        assertEquals(Locale.ENGLISH, LocaleUtils.getFallback(Locale.US));
        assertEquals(new Locale("ms"), LocaleUtils.getFallback(new Locale("pse")));
    }

    @Test
    public void testGetFallbacks() throws Exception {
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH}, LocaleUtils.getFallbacks(Locale.US));
        assertArrayEquals(new Locale[] {Locale.ENGLISH}, LocaleUtils.getFallbacks(Locale.ENGLISH));
        assertArrayEquals(new Locale[] {new Locale("pse"), new Locale("ms")},
                          LocaleUtils.getFallbacks(new Locale("pse")));

        // test batch fallback resolution
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH},
                          LocaleUtils.getFallbacks(Locale.US, Locale.ENGLISH));
        assertArrayEquals(new Locale[] {Locale.ENGLISH, Locale.US},
                          LocaleUtils.getFallbacks(Locale.ENGLISH, Locale.US));
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH, Locale.CANADA, Locale.CANADA_FRENCH, Locale.FRENCH},
                          LocaleUtils.getFallbacks(Locale.US, Locale.CANADA, Locale.CANADA_FRENCH));
    }
}
