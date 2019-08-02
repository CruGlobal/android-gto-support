package org.ccci.gto.android.common.compat.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

@RunWith(Parameterized::class)
internal class LocaleCompatTest(private val compat: LocaleCompatMethods) {

    @Test
    @Throws(Exception::class)
    fun testForLanguageTag() {
        for ((key, value) in LOCALES) {
            assertEquals(value, compat.forLanguageTag(key))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testToLanguageTag() {
        for ((key, value) in LANGUAGETAGS) {
            assertEquals(value, compat.toLanguageTag(key))
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<*>> {
            return listOf(
                arrayOf(BaseLocaleCompatMethods()),
                arrayOf(LollipopLocaleCompatMethods()),
                arrayOf(NougatLocaleCompatMethods())
            )
        }

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
    }
}
