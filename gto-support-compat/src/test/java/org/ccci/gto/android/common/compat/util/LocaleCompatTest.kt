package org.ccci.gto.android.common.compat.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.equalToIgnoringCase
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeThat
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
@Config(sdk = [16, 17, 18, 19, 21, 24])
internal class LocaleCompatTest {
    @Test
    fun testForLanguageTag() {
        LOCALES.forEach { (key, value) -> assertEquals(value, LocaleCompat.forLanguageTag(key)) }
    }

    @Test
    fun testForLanguageTagWithScript() {
        assumeScriptSupported()

        with(LocaleCompat.forLanguageTag("zh-Hans-CN")) {
            assertThat(this, equalTo(Locale.Builder().setLanguage("zh").setScript("Hans").setRegion("CN").build()))
            assertEquals("zh", language)
            assertEquals("Hans", script)
            assertEquals("CN", country)
        }
    }

    @Test
    fun testForLanguageTagWithScriptBeforeLollipop() {
        assumeScriptNotSupported()

        with(LocaleCompat.forLanguageTag("zh-Hans-CN")) {
            assertThat(this, equalTo(Locale("zh", "CN")))
            assertEquals("zh", language)
            assertEquals("CN", country)
        }
    }

    @Test
    fun testForLanguageTagWithExtension() {
        assumeExtensionSupported()

        with(LocaleCompat.forLanguageTag("en-x-US")) {
            assertEquals("en", language)
            assertEquals("", country)
            assertThat(getExtension('x'), equalToIgnoringCase("US"))
        }
    }

    @Test
    fun testForLanguageTagWithExtensionBeforeLollipop() {
        assumeExtensionNotSupported()

        with(LocaleCompat.forLanguageTag("en-x-US")) {
            assertEquals("en", language)
            assertEquals("", country)
        }
    }

    @Test
    fun testToLanguageTag() {
        LANGUAGETAGS.forEach { (key, value) -> assertEquals(value, LocaleCompat.toLanguageTag(key)) }
    }

    @Test
    fun testToLanguageTagWithScript() {
        assumeScriptSupported()
        assertEquals("zh-Hans-CN", LocaleCompat.toLanguageTag(Locale.forLanguageTag("zh-Hans-CN")))
    }
}

// script & extension support is only available starting with Lollipop
private fun assumeLollipop() = assumeThat(Build.VERSION.SDK_INT, greaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP))
private fun assumePreLollipop() = assumeThat(Build.VERSION.SDK_INT, lessThan(Build.VERSION_CODES.LOLLIPOP))
private fun assumeScriptSupported() = assumeLollipop()
private fun assumeScriptNotSupported() = assumePreLollipop()
private fun assumeExtensionSupported() = assumeLollipop()
private fun assumeExtensionNotSupported() = assumePreLollipop()
