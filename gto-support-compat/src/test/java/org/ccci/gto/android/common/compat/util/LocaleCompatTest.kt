package org.ccci.gto.android.common.compat.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private val LANGUAGETAGS = mapOf<Locale, String>(
    Locale.US to "en-US",
    Locale("EN", "gb") to "en-GB",
    Locale.ENGLISH to "en",
    Locale.SIMPLIFIED_CHINESE to "zh-CN",
    Locale("es", "419") to "es-419"
)

private val LOCALES = mapOf<String, Locale>(
    "en-US" to Locale.US,
    "en-GB" to Locale.UK,
    "en" to Locale.ENGLISH,
    "EN-us" to Locale.US,
    "es-419" to Locale("es", "419")
)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21, 24])
internal class LocaleCompatTest {
    @Test
    fun testForLanguageTag() {
        LOCALES.forEach { (key, value) -> assertEquals(value, LocaleCompat.forLanguageTag(key)) }
    }

    @Test
    fun testForLanguageTagWithScript() {
        with(LocaleCompat.forLanguageTag("zh-Hans-CN")) {
            assertThat(this, equalTo(Locale.Builder().setLanguage("zh").setScript("Hans").setRegion("CN").build()))
            assertEquals("zh", language)
            assertEquals("Hans", script)
            assertEquals("CN", country)
        }
    }

    @Test
    fun testForLanguageTagWithExtension() {
        with(LocaleCompat.forLanguageTag("en-x-US")) {
            assertEquals("en", language)
            assertEquals("", country)
            assertThat(getExtension('x'), equalToIgnoringCase("US"))
        }
    }

    @Test
    fun testToLanguageTag() {
        LANGUAGETAGS.forEach { (key, value) -> assertEquals(value, LocaleCompat.toLanguageTag(key)) }
    }

    @Test
    fun testToLanguageTagWithScript() {
        assertEquals("zh-Hans-CN", LocaleCompat.toLanguageTag(Locale.forLanguageTag("zh-Hans-CN")))
    }
}
