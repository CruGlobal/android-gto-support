package org.ccci.gto.android.common.kotlin.coroutines

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
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

    @Test
    fun testGetBooleanFlow() = runTest {
        val flowOutput = Channel<Boolean>(1)
        val flow = prefs.getBooleanFlow(KEY, false)
            .onEach { flowOutput.send(it) }
            .launchIn(this)

        assertFalse("Initially not set", flowOutput.receive())

        prefs.edit().putBoolean(KEY, true).apply()
        assertTrue("Toggled this pref to true", flowOutput.receive())

        prefs.edit().putBoolean(OTHER_KEY, false).apply()
        assertTrue("Different key update shouldn't trigger flow", flowOutput.isEmpty)

        prefs.edit().clear().apply()
        assertFalse("Preferences cleared", flowOutput.receive())

        prefs.edit().putBoolean(KEY, true).apply()
        flowOutput.receive()
        prefs.edit().putBoolean(KEY, false).apply()
        assertFalse("Toggled this pref to false", flowOutput.receive())

        flow.cancel()
    }
}
