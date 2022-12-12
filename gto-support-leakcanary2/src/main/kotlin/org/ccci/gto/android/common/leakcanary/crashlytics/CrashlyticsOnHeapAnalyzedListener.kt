package org.ccci.gto.android.common.leakcanary.crashlytics

import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import shark.HeapAnalysis
import shark.HeapAnalysisSuccess

@Deprecated("Since v3.11.2, use CrashlyticsEventListener with leakcanary 2.8.1+ instead.")
object CrashlyticsOnHeapAnalyzedListener : OnHeapAnalyzedListener {
    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        if (heapAnalysis is HeapAnalysisSuccess) CrashlyticsEventListener.sendLeaksToCrashlytics(heapAnalysis)
        defaultListener.onHeapAnalyzed(heapAnalysis)
    }
}
