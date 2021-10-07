package org.ccci.gto.android.common.snowplow.events

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.snowplowanalytics.snowplow.event.AbstractEvent
import com.snowplowanalytics.snowplow.event.Event
import com.snowplowanalytics.snowplow.internal.emitter.Emitter
import com.snowplowanalytics.snowplow.internal.emitter.Executor
import com.snowplowanalytics.snowplow.internal.tracker.Logger
import com.snowplowanalytics.snowplow.internal.tracker.Subject
import com.snowplowanalytics.snowplow.internal.tracker.Tracker
import com.snowplowanalytics.snowplow.payload.Payload
import java.util.concurrent.TimeUnit
import org.ccci.gto.android.common.snowplow.utils.TimberLogger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
abstract class CustomEventTest<B> where B : AbstractEvent.Builder<B>, B : CustomEventBuilder<B> {
    private lateinit var emitter: Emitter
    private lateinit var tree: Timber.Tree

    private lateinit var tracker: Tracker

    @Before
    fun setupTracker() {
        emitter = mock()
        tree = spy(Timber.DebugTree())
        tracker = Tracker.TrackerBuilder(emitter, "", "", mock())
            .subject(Subject.SubjectBuilder().build())
            .build()
        Logger.setDelegate(TimberLogger)
        Timber.plant(tree)
    }

    @After
    fun cleanupTracker() {
        Timber.uproot(tree)
        Logger.setDelegate(null)
        Tracker.close()
    }

    abstract fun eventBuilder(): B
    abstract fun B.buildEvent(): Event

    @Test
    fun testCustomAttributes() {
        val event = eventBuilder()
            .attribute("custom", "value")
            .attribute("null", null)
            .buildEvent()

        tracker.track(event)
        Executor.shutdown()?.awaitTermination(10, TimeUnit.SECONDS)
        argumentCaptor<Payload> {
            verify(emitter).add(capture())
            assertThat(firstValue.map, allOf(hasEntry("custom", "value"), not(hasKey("null"))))
        }
        verifyNoMoreInteractions(tree)
    }
}
