package org.ccci.gto.android.common.kotlin.coroutines

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val KEY = "key"
private const val OTHER_KEY = "other"

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FlowSharedPreferencesTest {
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        prefs = ApplicationProvider.getApplicationContext<Context>().getSharedPreferences("test", Context.MODE_PRIVATE)
    }

    // region getChangeFlow()
    @Test
    fun `getChangeFlow() - emit initial key`() = runTest {
        val initial = String()
        prefs.getChangeFlow(initial).test {
            runCurrent()
            assertSame(initial, awaitItem())
        }
    }

    @Test
    fun `getChangeFlow() - don't emit initial key`() = runTest {
        prefs.getChangeFlow().test {
            runCurrent()
            expectNoEvents()
        }
    }
    // endregion getChangeFlow()

    @Test
    fun testGetBooleanFlow() = runTest {
        prefs.getBooleanFlow(KEY, false).test {
            assertFalse("Initially not set", awaitItem())

            prefs.edit().putBoolean(KEY, true).apply()
            assertTrue("Toggled this pref to true", awaitItem())

            // Different key update shouldn't trigger flow
            prefs.edit().putBoolean(OTHER_KEY, false).apply()
            runCurrent()
            expectNoEvents()

            prefs.edit().clear().apply()
            assertFalse("Preferences cleared", awaitItem())

            prefs.edit().putBoolean(KEY, true).apply()
            awaitItem()
            prefs.edit().putBoolean(KEY, false).apply()
            assertFalse("Toggled this pref to false", awaitItem())
        }
    }

    @Test
    fun `getStringFlow() - Don't lose preference change while starting`() = runTest {
        val prefsSpy = spyk(prefs)
        every { prefsSpy.getString(KEY, "default") } answers {
            val value = callOriginal()
            if (value == "default") prefs.edit().putString(KEY, "set").apply()
            value
        }
        prefsSpy.getStringFlow(KEY, "default").test {
            runCurrent()
            assertEquals("set", expectMostRecentItem())
        }
    }
}
