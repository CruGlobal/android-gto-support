package org.ccci.gto.android.common.snowplow.events

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.snowplowanalytics.snowplow.internal.emitter.Emitter
import com.snowplowanalytics.snowplow.internal.emitter.Executor
import com.snowplowanalytics.snowplow.internal.tracker.Logger
import com.snowplowanalytics.snowplow.internal.tracker.Subject
import com.snowplowanalytics.snowplow.internal.tracker.Tracker
import com.snowplowanalytics.snowplow.payload.Payload
import java.util.concurrent.TimeUnit
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.Shadows
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
abstract class CustomEventTest<E : CustomEvent<E>> {
    private lateinit var emitter: Emitter
    private lateinit var tree: Timber.Tree

    private lateinit var tracker: Tracker

    @Before
    fun setupTracker() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Shadows.shadowOf(context.getSystemService(ConnectivityManager::class.java)).setActiveNetworkInfo(null)
        emitter = mock()
        tree = spy(Timber.DebugTree())
        tracker = Tracker(Tracker.TrackerBuilder(emitter, "", "", context).subject(Subject(context, null)))
        Timber.plant(tree)
    }

    @After
    fun cleanupTracker() {
        Timber.uproot(tree)
        Logger.setDelegate(null)
        tracker.close()
    }

    abstract fun event(): E

    @Test
    fun testCustomAttributes() {
        val event = event()
            .attribute("custom", "value")
            .attribute("null", null)

        tracker.track(event)
        Executor.shutdown()?.awaitTermination(10, TimeUnit.SECONDS)
        argumentCaptor<Payload> {
            verify(emitter).add(capture())
            assertThat(firstValue.map, allOf(hasEntry("custom", "value"), not(hasKey("null"))))
        }
        verifyNoInteractions(tree)
    }
}
