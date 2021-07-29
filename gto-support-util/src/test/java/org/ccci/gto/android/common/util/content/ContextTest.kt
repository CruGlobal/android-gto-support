package org.ccci.gto.android.common.util.content

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.ccci.gto.android.common.util.os.toTypedArray
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.arrayContaining
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.TARGET_SDK

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21, 23, 24, TARGET_SDK])
class ContextTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get()
    }

    @Test
    fun verifyLocalizeSdk21() {
        assumeTrue(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)

        context.resources.configuration.setLocale(Locale.ENGLISH)
        assertEquals(Locale.ENGLISH, context.localize().resources.configuration.locale)
        assertEquals(Locale.FRENCH, context.localize(Locale.FRENCH).resources.configuration.locale)
    }

    @Test
    fun verifyLocalizeSdk24() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

        context.resources.configuration.setLocales(LocaleList(Locale.ENGLISH, Locale.GERMAN))
        assertThat(
            context.localize(Locale.FRENCH).resources.configuration.locales.toTypedArray(),
            arrayContaining(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN)
        )
        assertThat(
            context.localize(Locale.FRENCH, includeExisting = false).resources.configuration.locales.toTypedArray(),
            arrayContaining(Locale.FRENCH)
        )
        assertThat(
            context.localize().resources.configuration.locales.toTypedArray(),
            arrayContaining(Locale.ENGLISH, Locale.GERMAN)
        )
    }

    @Test
    fun verifyLocalizeDuplicateLocale() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

        context.resources.configuration.setLocales(LocaleList(Locale.ENGLISH))
        assertThat(
            context.localize(Locale.ENGLISH).resources.configuration.locales.toTypedArray(),
            arrayContaining(Locale.ENGLISH)
        )
    }
}
