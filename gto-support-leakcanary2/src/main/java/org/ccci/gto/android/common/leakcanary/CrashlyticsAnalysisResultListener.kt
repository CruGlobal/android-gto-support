package org.ccci.gto.android.common.leakcanary

import com.crashlytics.android.Crashlytics
import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import org.ccci.gto.android.common.leakcanary.util.asFakeException
import shark.HeapAnalysis
import shark.HeapAnalysisSuccess

private const val LOG_LIMIT = 64 * 1024

@Deprecated(
    "Since v3.6.2, this Listener uses the deprecated Fabric Crashlytics library that will no longer be supported" +
        " starting November 15th, 2020"
)
class CrashlyticsOnHeapAnalyzedListener : OnHeapAnalyzedListener {
    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        if (heapAnalysis is HeapAnalysisSuccess) {
            heapAnalysis.allLeaks.asSequence().flatMap { it.leakTraces.asSequence() }.forEach { leak ->
                // log the memory leak to Crashlytics
                Crashlytics.log("*** Memory Leak ***")
                leak.toString().take(LOG_LIMIT).lines().asSequence().filterNot { it.isEmpty() }.forEach {
                    Crashlytics.log(it)
                }

                Crashlytics.logException(leak.asFakeException())
            }
        }

        defaultListener.onHeapAnalyzed(heapAnalysis)
    }
}
