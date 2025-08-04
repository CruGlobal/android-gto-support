package org.ccci.gto.android.common.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

private val MALAY = Locale.forLanguageTag("ms")
private val BENGKULU = Locale.forLanguageTag("pse")

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.N, NEWEST_SDK])
class LocaleRobolectricTest {
    // region Locale.fallback
    @Test
    fun verifyFallback() {
        assertNull(Locale.ENGLISH.fallback)
        assertEquals(Locale.ENGLISH, Locale.US.fallback)
        assertEquals(MALAY, BENGKULU.fallback)
    }

    @Test
    fun `verifyFallback - With Script`() {
        assertEquals(Locale.CHINESE, Locale.forLanguageTag("zh-Hans").fallback)
    }

    @Test
    fun `verifyFallback - With Extension`() {
        assertEquals(Locale.ENGLISH, Locale.forLanguageTag("en-x-private").fallback)
    }
    // endregion Locale.fallback

    // region Locale.fallbacks
    @Test
    fun verifyFallbacks() {
        assertEquals(listOf(Locale.ENGLISH), Locale.US.fallbacks.toList())
        assertEquals(emptyList<Locale>(), Locale.ENGLISH.fallbacks.toList())
        assertEquals(listOf(MALAY), BENGKULU.fallbacks.toList())
    }
    @Test
    fun `verifyFallbacks - With Script`() {
        assertEquals(
            listOf(Locale.forLanguageTag("zh-Hans"), Locale.CHINESE),
            Locale.forLanguageTag("zh-Hans-CN").fallbacks.toList()
        )
    }
    // endregion Locale.fallbacks

    // region Sequence<Locale>.includeFallbacks()
    @Test
    fun `verifyIncludeFallbacks - With Extension`() {
        val locale = Locale.forLanguageTag("vn-VN-x-thefour")

        assertEquals(
            listOf(locale, Locale.forLanguageTag("vn-VN"), Locale("vn")),
            sequenceOf(locale).includeFallbacks().toList()
        )
    }

    @Test
    fun `verifyIncludeFallbacks - Multiple Locales`() {
        assertEquals(
            listOf(Locale.US, Locale.ENGLISH, Locale.UK, Locale.ENGLISH, Locale.CANADA_FRENCH, Locale.FRENCH),
            sequenceOf(Locale.US, Locale.UK, Locale.CANADA_FRENCH).includeFallbacks().toList()
        )
    }
    // endregion Sequence<Locale>.includeFallbacks()
}
