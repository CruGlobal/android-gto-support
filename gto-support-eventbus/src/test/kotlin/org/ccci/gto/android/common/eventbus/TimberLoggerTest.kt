package org.ccci.gto.android.common.eventbus

import android.util.Log
import java.util.logging.Level
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class TimberLoggerTest {
    @Test
    fun testMapLevel() {
        assertThat(TimberLogger.mapLevel(Level.SEVERE), equalTo(Log.ERROR))
        assertThat(TimberLogger.mapLevel(Level.WARNING), equalTo(Log.WARN))
        assertThat(TimberLogger.mapLevel(Level.INFO), equalTo(Log.INFO))
        assertThat(TimberLogger.mapLevel(Level.CONFIG), equalTo(Log.DEBUG))
        assertThat(TimberLogger.mapLevel(Level.FINE), equalTo(Log.VERBOSE))
        assertThat(TimberLogger.mapLevel(Level.FINER), equalTo(Log.VERBOSE))
        assertThat(TimberLogger.mapLevel(Level.FINEST), equalTo(Log.VERBOSE))
    }
}
