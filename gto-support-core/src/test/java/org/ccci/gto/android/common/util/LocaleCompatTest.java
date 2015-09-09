package org.ccci.gto.android.common.util;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class LocaleCompatTest {
    @Test
    public void testToLanguageTagPreLollipop() throws Exception {
        assertEquals("en-US", LocaleCompat.toLanguageTagPreLollipop(Locale.US));
        assertEquals("zh-CN", LocaleCompat.toLanguageTagPreLollipop(Locale.SIMPLIFIED_CHINESE));
    }
}
