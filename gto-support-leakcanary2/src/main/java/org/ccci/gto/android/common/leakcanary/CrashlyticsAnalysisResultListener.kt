package org.ccci.gto.android.common.leakcanary

import com.crashlytics.android.Crashlytics
import leakcanary.OnHeapAnalyzedListener
import shark.HeapAnalysis
import shark.HeapAnalysisSuccess

private const val LOG_LIMIT = 64 * 1024

class CrashlyticsOnHeapAnalyzedListener : OnHeapAnalyzedListener {
    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        if (heapAnalysis !is HeapAnalysisSuccess) return
        if (heapAnalysis.allLeaks.isEmpty()) return

        // log the memory leak to Crashlytics
        Crashlytics.log("*** Memory Leak ***")
        heapAnalysis.toString().take(LOG_LIMIT).lines().asSequence().filterNot { it.isEmpty() }.forEach {
            Crashlytics.log(it)
        }

        Crashlytics.logException(RuntimeException("Memory Leak"))
    }
}
