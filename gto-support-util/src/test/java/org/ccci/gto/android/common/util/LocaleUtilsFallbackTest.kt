package org.ccci.gto.android.common.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.Assume.assumeThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private val MALAY = LocaleCompat.forLanguageTag("ms")
private val BENGKULU = LocaleCompat.forLanguageTag("pse")

@RunWith(AndroidJUnit4::class)
@Config(sdk = [16, 17, 18, 19, 21, 24])
class LocaleUtilsFallbackTest {
    @Test
    fun verifyGetFallback() {
        assertThat(LocaleUtils.getFallback(Locale.US), equalTo(Locale.ENGLISH))
        assertThat(LocaleUtils.getFallback(BENGKULU), equalTo(MALAY))
    }

    @Test
    fun verifyGetFallbackWithScript() {
        assumeScriptSupported()

        assertThat(LocaleUtils.getFallback(LocaleCompat.forLanguageTag("zh-Hans")), equalTo(Locale.CHINESE))
    }

    @Test
    fun verifyGetFallbackWithExtensions() {
        assumeExtensionSupported()

        assertThat(LocaleUtils.getFallback(LocaleCompat.forLanguageTag("en-x-private")), equalTo(Locale.ENGLISH))
    }

    @Test
    fun verifyGetFallbacks() {
        assertThat(LocaleUtils.getFallbacks(Locale.US), equalTo(arrayOf(Locale.US, Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(Locale.ENGLISH), equalTo(arrayOf(Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(BENGKULU), equalTo(arrayOf(BENGKULU, MALAY)))
    }

    @Test
    fun verifyGetFallbacksWithScript() {
        assumeScriptSupported()

        val locale = LocaleCompat.forLanguageTag("zh-Hans-CN")
        assertThat(
            LocaleUtils.getFallbacks(locale),
            equalTo(arrayOf(locale, LocaleCompat.forLanguageTag("zh-Hans"), Locale.CHINESE))
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

// script & extension support is only available starting with Lollipop
private fun assumeLollipop() = assumeThat(Build.VERSION.SDK_INT, greaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP))
private fun assumeScriptSupported() = assumeLollipop()
private fun assumeExtensionSupported() = assumeLollipop()
