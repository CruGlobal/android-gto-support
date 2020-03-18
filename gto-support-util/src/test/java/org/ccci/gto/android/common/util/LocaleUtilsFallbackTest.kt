package org.ccci.gto.android.common.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.ccci.gto.android.common.compat.util.LocaleCompat
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale

private val MALAY = LocaleCompat.forLanguageTag("ms")
private val BENGKULU = LocaleCompat.forLanguageTag("pse")

@RunWith(AndroidJUnit4::class)
@Config(sdk = [16, 17, 18, 19, 21, 24])
class LocaleUtilsFallbackTest {
    @Test
    fun verifyGetFallback() {
        assertThat(LocaleUtils.getFallback(Locale.US), CoreMatchers.`is`(Locale.ENGLISH))
        assertThat(LocaleUtils.getFallback(LocaleCompat.forLanguageTag("en-x-private")), equalTo(Locale.ENGLISH))
        assertThat(LocaleUtils.getFallback(BENGKULU), equalTo(MALAY))
    }

    @Test
    fun verifyGetFallbacks() {
        assertThat(LocaleUtils.getFallbacks(Locale.US), equalTo(arrayOf(Locale.US, Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(Locale.ENGLISH), equalTo(arrayOf(Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(BENGKULU), equalTo(arrayOf(BENGKULU, MALAY)))
    }

    @Test
    fun verifyGetFallbacksMulti() {
        // test batch fallback resolution
        assertThat(LocaleUtils.getFallbacks(Locale.US, Locale.ENGLISH), equalTo(arrayOf(Locale.US, Locale.ENGLISH)))
        assertThat(LocaleUtils.getFallbacks(Locale.ENGLISH, Locale.US), equalTo(arrayOf(Locale.ENGLISH, Locale.US)))
        assertThat(
            LocaleUtils.getFallbacks(Locale.US, Locale.CANADA, Locale.CANADA_FRENCH),
            equalTo(arrayOf(Locale.US, Locale.ENGLISH, Locale.CANADA, Locale.CANADA_FRENCH, Locale.FRENCH))
        )
    }
}
