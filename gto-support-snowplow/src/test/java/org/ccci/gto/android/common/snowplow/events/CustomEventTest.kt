package org.ccci.gto.android.common.snowplow.events

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.snowplowanalytics.snowplow.tracker.Emitter
import com.snowplowanalytics.snowplow.tracker.Executor
import com.snowplowanalytics.snowplow.tracker.Subject.SubjectBuilder
import com.snowplowanalytics.snowplow.tracker.Tracker
import com.snowplowanalytics.snowplow.tracker.events.AbstractEvent
import com.snowplowanalytics.snowplow.tracker.events.Event
import com.snowplowanalytics.snowplow.tracker.payload.Payload
import com.snowplowanalytics.snowplow.tracker.utils.Logger
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
            .subject(SubjectBuilder().build())
            .build()
        Logger.setErrorLogger(TimberLogger)
        Timber.plant(tree)
    }

    @After
    fun cleanupTracker() {
        Timber.uproot(tree)
        Logger.setErrorLogger(null)
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
        Executor.shutdown().awaitTermination(10, TimeUnit.SECONDS)
        argumentCaptor<Payload> {
            verify(emitter).add(capture())
            assertThat(firstValue.map, allOf(hasEntry("custom", "value"), not(hasKey("null"))))
        }
        verifyZeroInteractions(tree)
    }
}
