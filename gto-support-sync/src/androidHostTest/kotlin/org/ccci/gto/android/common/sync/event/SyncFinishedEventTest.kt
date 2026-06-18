package org.ccci.gto.android.common.sync.event

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncFinishedEventTest {
    @Test
    fun verifySyncIdPersistence() {
        val event = SyncFinishedEvent(5)
        assertEquals(5, event.syncId)
    }
}
