package org.ccci.gto.android.common.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
class LocaleRobolectricTest {
    @Test
    @Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.N, NEWEST_SDK])
    fun verifyIncludeFallbacksSequenceExtensions() {
        val locale = Locale.forLanguageTag("vn-VN-x-thefour")

        assertEquals(
            listOf(locale, Locale.forLanguageTag("vn-VN"), Locale("vn")),
            sequenceOf(locale).includeFallbacks().toList()
        )
    }
}
