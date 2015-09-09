package org.ccci.gto.android.common.util;

import static junit.framework.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.ccci.gto.android.common.util.LocaleCompat.FroyoLocale;
import org.ccci.gto.android.common.util.LocaleCompat.LollipopLocale;
import org.junit.Test;

import java.util.Locale;
import java.util.Map;

public class LocaleCompatTest {
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

    @Test
    public void testFroyoLocaleForLanguageTag() throws Exception {
        for (final Map.Entry<String, Locale> entry : LOCALES.entrySet()) {
            assertEquals(entry.getValue(), FroyoLocale.forLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testFroyoLocaleToLanguageTag() throws Exception {
        for (final Map.Entry<Locale, String> entry : LANGUAGETAGS.entrySet()) {
            assertEquals(entry.getValue(), FroyoLocale.toLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testLollipopLocaleForLanguageTag() throws Exception {
        for (final Map.Entry<String, Locale> entry : LOCALES.entrySet()) {
            assertEquals(entry.getValue(), LollipopLocale.forLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testLollipopLocaleToLanguageTag() throws Exception {
        for (final Map.Entry<Locale, String> entry : LANGUAGETAGS.entrySet()) {
            assertEquals(entry.getValue(), LollipopLocale.toLanguageTag(entry.getKey()));
        }
    }
}
