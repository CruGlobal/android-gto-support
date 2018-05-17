package org.ccci.gto.android.common.compat.util;

import com.google.common.collect.ImmutableMap;

import org.ccci.gto.android.common.compat.util.LocaleCompat.Compat;
import org.ccci.gto.android.common.compat.util.LocaleCompat.FroyoCompat;
import org.ccci.gto.android.common.compat.util.LocaleCompat.LollipopCompat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LocaleCompatTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{new FroyoCompat()}, {new LollipopCompat()}});
    }

    private final Compat compat;

    private static final Map<Locale, String> LANGUAGETAGS;
    static {
        final ImmutableMap.Builder<Locale, String> builder = ImmutableMap.builder();
        builder.put(Locale.US, "en-US");
        builder.put(new Locale("EN", "gb"), "en-GB");
        builder.put(Locale.ENGLISH, "en");
        builder.put(Locale.SIMPLIFIED_CHINESE, "zh-CN");
        LANGUAGETAGS = builder.build();
    }

    private static final Map<String, Locale> LOCALES;
    static {
        final ImmutableMap.Builder<String, Locale> builder = ImmutableMap.builder();
        builder.put("en-US", Locale.US);
        builder.put("en-GB", Locale.UK);
        builder.put("en", Locale.ENGLISH);
        builder.put("EN-us", Locale.US);
        LOCALES = builder.build();
    }

    public LocaleCompatTest(final Compat compat) {
        this.compat = compat;
    }

    @Test
    public void testForLanguageTag() throws Exception {
        for (final Map.Entry<String, Locale> entry : LOCALES.entrySet()) {
            assertEquals(entry.getValue(), compat.forLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testToLanguageTag() throws Exception {
        for (final Map.Entry<Locale, String> entry : LANGUAGETAGS.entrySet()) {
            assertEquals(entry.getValue(), compat.toLanguageTag(entry.getKey()));
        }
    }
}
