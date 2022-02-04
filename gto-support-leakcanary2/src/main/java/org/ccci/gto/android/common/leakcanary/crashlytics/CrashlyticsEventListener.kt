package org.ccci.gto.android.common.leakcanary.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import leakcanary.EventListener
import org.ccci.gto.android.common.leakcanary.util.asFakeException
import shark.HeapAnalysisSuccess

private const val LOG_LIMIT = 64 * 1024

object CrashlyticsEventListener : EventListener {
    private val crashlytics get() = FirebaseCrashlytics.getInstance()

    override fun onEvent(event: EventListener.Event) = when (event) {
        is EventListener.Event.HeapAnalysisDone.HeapAnalysisSucceeded -> sendLeaksToCrashlytics(event.heapAnalysis)
        else -> Unit
    }

    internal fun sendLeaksToCrashlytics(heapAnalysis: HeapAnalysisSuccess) {
        heapAnalysis.allLeaks.asSequence().flatMap { it.leakTraces.asSequence() }.forEach { leak ->
            // log the memory leak to Crashlytics
            crashlytics.log("*** Memory Leak ***")
            leak.toString().take(LOG_LIMIT).lines().asSequence()
                .filterNot { it.isEmpty() }
                .forEach { crashlytics.log(it) }

            crashlytics.recordException(leak.asFakeException())
        }
    }
}
