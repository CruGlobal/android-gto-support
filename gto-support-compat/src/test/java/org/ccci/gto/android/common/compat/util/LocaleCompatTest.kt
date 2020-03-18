package org.ccci.gto.android.common.compat.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale

private val LANGUAGETAGS = mapOf<Locale, String>(
    Locale.US to "en-US",
    Locale("EN", "gb") to "en-GB",
    Locale.ENGLISH to "en",
    Locale.SIMPLIFIED_CHINESE to "zh-CN"
)

private val LOCALES = mapOf<String, Locale>(
    "en-US" to Locale.US,
    "en-GB" to Locale.UK,
    "en" to Locale.ENGLISH,
    "EN-us" to Locale.US
)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [16, 17, 18, 19, 21, 24])
internal class LocaleCompatTest {
    @Test
    fun testForLanguageTag() {
        for ((key, value) in LOCALES) {
            assertEquals(value, LocaleCompat.forLanguageTag(key))
        }
    }

    @Test
    fun testToLanguageTag() {
        for ((key, value) in LANGUAGETAGS) {
            assertEquals(value, LocaleCompat.toLanguageTag(key))
        }
    }
}
