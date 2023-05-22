package org.ccci.gto.android.common.androidx.core.app

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
class LocaleConfigCompatTest {
    @Test
    @Ignore("Robolectric doesn't appear to currently support the LocaleConfig object")
    @Config(sdk = [Build.VERSION_CODES.TIRAMISU, NEWEST_SDK])
    fun `getSupportedLocales()`() = testGetSupportedLocales()

    @Test
    @Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.S_V2])
    fun `getSupportedLocales() - Compat mode`() = testGetSupportedLocales()

    private fun testGetSupportedLocales() {
        val locales = LocaleConfigCompat.getSupportedLocales(ApplicationProvider.getApplicationContext())
        assertNotNull(locales)
        assertEquals(Locale.ENGLISH, locales?.get(0))
        assertEquals(Locale.FRENCH, locales?.get(1))
    }
}
