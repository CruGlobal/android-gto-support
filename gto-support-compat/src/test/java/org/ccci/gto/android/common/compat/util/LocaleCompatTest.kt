package org.ccci.gto.android.common.compat.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale

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
@Config(sdk = [16, 17, 18, 19, 21, 24])
internal class LocaleCompatTest {
    @Test
    fun testForLanguageTag() {
        LOCALES.forEach { (key, value) -> assertEquals(value, LocaleCompat.forLanguageTag(key)) }
    }

    @Test
    fun testForLanguageTagWithScript() {
        // script support is only available starting with Lollipop
        assumeThat(Build.VERSION.SDK_INT, greaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP))

        val locale = LocaleCompat.forLanguageTag("zh-Hans-CN")
        assertThat(locale, equalTo(Locale.Builder().setLanguage("zh").setScript("Hans").setRegion("CN").build()))
        assertEquals("zh", locale.language)
        assertEquals("Hans", locale.script)
        assertEquals("CN", locale.country)
    }

    @Test
    fun testForLanguageTagWithScriptBeforeLollipop() {
        // script support is only available starting with Lollipop
        assumeThat(Build.VERSION.SDK_INT, lessThan(Build.VERSION_CODES.LOLLIPOP))

        val locale = LocaleCompat.forLanguageTag("zh-Hans-CN")
        assertThat(locale, equalTo(Locale("zh", "CN")))
        assertEquals("zh", locale.language)
        assertEquals("CN", locale.country)
    }

    @Test
    fun testToLanguageTag() {
        LANGUAGETAGS.forEach { (key, value) -> assertEquals(value, LocaleCompat.toLanguageTag(key)) }
    }

    @Test
    fun testToLanguageTagWithScript() {
        // script support is only available starting with Lollipop
        assumeThat(Build.VERSION.SDK_INT, greaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP))

        assertEquals("zh-Hans-CN", LocaleCompat.toLanguageTag(Locale.forLanguageTag("zh-Hans-CN")))
    }
}
