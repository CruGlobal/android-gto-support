package org.ccci.gto.android.common.compat.app

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.P, Build.VERSION_CODES.Q, NEWEST_SDK])
class ActivityTest {
    @get:Rule
    val activityScenario = ActivityScenarioRule(Activity::class.java)

    private lateinit var callbacks: Application.ActivityLifecycleCallbacks

    @Before
    fun setup() {
        callbacks = mock()
    }

    @Test
    fun `ActivityLifecycleCallbacks - unregisterActivityLifecycleCallbacksCompat`() {
        with(activityScenario.scenario) {
            onActivity {
                // attach callbacks to the activity
                moveToState(Lifecycle.State.STARTED)
                it.registerActivityLifecycleCallbacksCompat(callbacks)
                moveToState(Lifecycle.State.RESUMED)
                verify(callbacks).onActivityResumed(it)

                // remove callbacks from activity
                it.unregisterActivityLifecycleCallbacksCompat(callbacks)
                clearInvocations(callbacks)
                moveToState(Lifecycle.State.STARTED)
                moveToState(Lifecycle.State.RESUMED)
                verifyNoInteractions(callbacks)
            }
        }
    }

    @Test
    fun `ActivityLifecycleCallbacks - callbacks cleared on re-creation`() {
        with(activityScenario.scenario) {
            // attach callbacks to the initial activity
            onActivity { it.registerActivityLifecycleCallbacksCompat(callbacks) }

            // when we recreate the activity, the destroy callback should be triggered,
            // but there shouldn't be any startup callbacks triggered
            recreate()
            onActivity {
                assertEquals(Lifecycle.State.RESUMED, state)
                verify(callbacks).onActivityDestroyed(any())
                verify(callbacks, never()).onActivityCreated(any(), any())
                verify(callbacks, never()).onActivityStarted(any())
                verify(callbacks, never()).onActivityResumed(any())
            }
        }
    }

    @Test
    fun `ActivityLifecycleCallbacks - onActivityResumed`() {
        with(activityScenario.scenario) {
            moveToState(Lifecycle.State.STARTED)
            onActivity { it.registerActivityLifecycleCallbacksCompat(callbacks) }
            moveToState(Lifecycle.State.RESUMED)
            verify(callbacks).onActivityResumed(any())
        }
    }
}
