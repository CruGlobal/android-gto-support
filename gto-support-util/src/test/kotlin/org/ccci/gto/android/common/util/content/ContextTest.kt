package org.ccci.gto.android.common.util.content

import android.app.Activity
import android.app.AliasActivity
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.ccci.gto.android.common.util.os.toTypedArray
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.arrayContaining
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, 23, 24, NEWEST_SDK])
class ContextTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get()
    }

    // region findActivity()
    @Test
    fun `findActivity()`() {
        val activity = context.findActivity()
        assertNotNull(activity)
        assertFailsWith<IllegalStateException> {
            context.applicationContext.findActivity()
        }
    }

    @Test
    fun `findActivity() - reified inline`() {
        assertNotNull(context.findActivity<Activity>())
        assertFailsWith<IllegalStateException> {
            context.applicationContext.findActivity<Activity>()
        }
        assertFailsWith<IllegalStateException> {
            context.findActivity<AliasActivity>()
        }

        val context2 = Robolectric.buildActivity(AliasActivity::class.java).get()
        assertNotNull(context2.findActivity<AliasActivity>())
    }

    @Test
    fun `findActivityOrNull()`() {
        val activity = context.findActivityOrNull()
        val activity2 = context.applicationContext.findActivityOrNull()
        assertNotNull(activity)
        assertNull(activity2)
    }

    @Test
    fun `findActivityOrNull() - reified inline`() {
        assertNotNull(context.findActivityOrNull<Activity>())
        assertNull(context.applicationContext.findActivityOrNull<Activity>())
        assertNull(context.findActivityOrNull<AliasActivity>())

        val context2 = Robolectric.buildActivity(AliasActivity::class.java).get()
        assertNotNull(context2.findActivityOrNull<Activity>())
        assertNotNull(context2.findActivityOrNull<AliasActivity>())
    }
    // endregion findActivity()

    @Test
    @Config(sdk = [OLDEST_SDK, 23])
    fun `localize() - SDK 21`() {
        assertTrue(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)

        context.resources.configuration.setLocale(Locale.ENGLISH)
        assertEquals(Locale.ENGLISH, context.localize().resources.configuration.locale)
        assertEquals(Locale.ENGLISH, context.localize(Locale.ENGLISH).resources.configuration.locale)
        assertEquals(Locale.FRENCH, context.localize(Locale.FRENCH).resources.configuration.locale)
        assertEquals(Locale.FRENCH, context.localize(Locale.FRENCH, Locale.ENGLISH).resources.configuration.locale)
    }

    @Test
    @Config(sdk = [24, NEWEST_SDK])
    fun `localize() - SDK 24`() {
        assertTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

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
    @Config(sdk = [24, NEWEST_SDK])
    fun `localize() - Duplicate Locales`() {
        assertTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

        context.resources.configuration.setLocales(LocaleList(Locale.ENGLISH))
        assertThat(
            context.localize(Locale.ENGLISH).resources.configuration.locales.toTypedArray(),
            arrayContaining(Locale.ENGLISH)
        )
    }
}
