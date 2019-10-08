package org.ccci.gto.android.common.leakcanary

import com.crashlytics.android.Crashlytics
import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import org.ccci.gto.android.common.leakcanary.util.asFakeException
import shark.HeapAnalysis
import shark.HeapAnalysisSuccess

private const val LOG_LIMIT = 64 * 1024

class CrashlyticsOnHeapAnalyzedListener : OnHeapAnalyzedListener {
    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        if (heapAnalysis is HeapAnalysisSuccess) {
            heapAnalysis.allLeaks.forEach { leak ->
                // log the memory leak to Crashlytics
                Crashlytics.log("*** Memory Leak ***")
                leak.leakTrace.toString().take(LOG_LIMIT).lines().asSequence().filterNot { it.isEmpty() }.forEach {
                    Crashlytics.log(it)
                }

                Crashlytics.logException(leak.asFakeException())
            }
        }

        defaultListener.onHeapAnalyzed(heapAnalysis)
    }
}
