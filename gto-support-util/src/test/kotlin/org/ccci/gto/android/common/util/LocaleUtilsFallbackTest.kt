package org.ccci.gto.android.common.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

private val MALAY = Locale.forLanguageTag("ms")
private val BENGKULU = Locale.forLanguageTag("pse")

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, 24, NEWEST_SDK])
class LocaleUtilsFallbackTest {
    @Test
    fun verifyGetFallback() {
        assertNull(LocaleUtils.getFallback(Locale.ENGLISH))
        assertThat(LocaleUtils.getFallback(Locale.US), equalTo(Locale.ENGLISH))
        assertThat(LocaleUtils.getFallback(BENGKULU), equalTo(MALAY))
    }

    @Test
    fun verifyGetFallbackWithScript() {
        assertThat(LocaleUtils.getFallback(Locale.forLanguageTag("zh-Hans")), equalTo(Locale.CHINESE))
    }

    @Test
    fun verifyGetFallbackWithExtensions() {
        assertThat(LocaleUtils.getFallback(Locale.forLanguageTag("en-x-private")), equalTo(Locale.ENGLISH))
    }

    @Test
    fun verifyGetFallbacks() {
        assertThat(LocaleUtils.getFallbacks(Locale.US), equalTo(arrayOf(Locale.US, Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(Locale.ENGLISH), equalTo(arrayOf(Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(BENGKULU), equalTo(arrayOf(BENGKULU, MALAY)))
    }

    @Test
    fun verifyGetFallbacksWithScript() {
        val locale = Locale.forLanguageTag("zh-Hans-CN")
        assertThat(
            LocaleUtils.getFallbacks(locale),
            equalTo(arrayOf(locale, Locale.forLanguageTag("zh-Hans"), Locale.CHINESE))
        )
    }

    @Test
    fun verifyGetFallbacksMulti() {
        assertThat(LocaleUtils.getFallbacks(Locale.US, Locale.ENGLISH), equalTo(arrayOf(Locale.US, Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(Locale.ENGLISH, Locale.US), equalTo(arrayOf(Locale.ENGLISH, Locale.US)))
        assertThat(
            LocaleUtils.getFallbacks(Locale.US, Locale.CANADA, Locale.CANADA_FRENCH),
            equalTo(arrayOf(Locale.US, Locale.ENGLISH, Locale.CANADA, Locale.CANADA_FRENCH, Locale.FRENCH))
        )
    }
}
