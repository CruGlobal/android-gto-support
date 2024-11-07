package org.ccci.gto.android.common.circuit.overlay

import com.slack.circuit.overlay.OverlayNavigator
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class TestOverlayNavigator<Result : Any> : OverlayNavigator<Result> {
    private val semaphore = Semaphore(1, 1)
    lateinit var result: Result
        private set

    override fun finish(result: Result) {
        require(!::result.isInitialized)
        this.result = result
        semaphore.release()
    }

    suspend fun awaitResult() = semaphore.withPermit { result }
}
