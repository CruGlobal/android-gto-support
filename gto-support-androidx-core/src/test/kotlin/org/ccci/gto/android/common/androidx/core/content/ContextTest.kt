package org.ccci.gto.android.common.androidx.core.content

import android.app.Activity
import android.content.Context
import android.os.LocaleList
import androidx.core.os.LocaleListCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.ccci.gto.android.common.util.os.toTypedArray
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
class ContextTest {
    private lateinit var context: Context

    @BeforeTest
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get()
    }

    // region localize()
    @Test
    @Config(sdk = [OLDEST_SDK, 23])
    fun verifyLocalizeLocaleListCompatSdk21() {
        context.resources.configuration.setLocale(Locale.ENGLISH)

        assertEquals(Locale.ENGLISH, context.localize(LocaleListCompat.create()).resources.configuration.locale)
        assertEquals(
            Locale.FRENCH,
            context.localize(LocaleListCompat.create(Locale.FRENCH, Locale.GERMAN)).resources.configuration.locale
        )
    }

    @Test
    @Config(sdk = [24, NEWEST_SDK])
    fun verifyLocalizeLocaleListCompatSdk24() {
        context.resources.configuration.setLocales(LocaleList(Locale.ENGLISH, Locale.GERMAN))

        val localizedContext = context.localize(LocaleListCompat.create(Locale.GERMAN, Locale.FRENCH))
        assertContentEquals(
            arrayOf(Locale.GERMAN, Locale.FRENCH, Locale.ENGLISH),
            localizedContext.resources.configuration.locales.toTypedArray()
        )
    }
    // endregion localize()
}
