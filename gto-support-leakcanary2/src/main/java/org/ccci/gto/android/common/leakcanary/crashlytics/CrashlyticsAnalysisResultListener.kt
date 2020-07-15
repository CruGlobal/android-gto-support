package org.ccci.gto.android.common.leakcanary.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import org.ccci.gto.android.common.leakcanary.util.asFakeException
import shark.HeapAnalysis
import shark.HeapAnalysisSuccess

private const val LOG_LIMIT = 64 * 1024

object CrashlyticsOnHeapAnalyzedListener : OnHeapAnalyzedListener {
    private val crashlytics get() = FirebaseCrashlytics.getInstance()
    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        if (heapAnalysis is HeapAnalysisSuccess) {
            heapAnalysis.allLeaks.asSequence().flatMap { it.leakTraces.asSequence() }.forEach { leak ->
                // log the memory leak to Crashlytics
                crashlytics.log("*** Memory Leak ***")
                leak.toString().take(LOG_LIMIT).lines().asSequence()
                    .filterNot { it.isEmpty() }
                    .forEach { crashlytics.log(it) }

                crashlytics.recordException(leak.asFakeException())
            }
        }

        defaultListener.onHeapAnalyzed(heapAnalysis)
    }
}
