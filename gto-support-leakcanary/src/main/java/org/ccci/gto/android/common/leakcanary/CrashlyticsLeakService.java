package org.ccci.gto.android.common.leakcanary;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.HeapDump;

import androidx.annotation.NonNull;

public class CrashlyticsLeakService extends DisplayLeakService {
    private static final int LOG_LIMIT = 64 * 1024;

    @NonNull
    private static String classSimpleName(@NonNull final String className) {
        int separator = className.lastIndexOf('.');
        return separator == -1 ? className : className.substring(separator + 1);
    }

    protected void afterDefaultHandling(@NonNull final HeapDump heapDump, @NonNull final AnalysisResult result,
                                        @NonNull String leakInfo) {
        if (!result.leakFound || result.excludedLeak) {
            return;
        }

        // truncate the leakInfo if it's too long for crashlytics
        if (leakInfo.length() > LOG_LIMIT) {
            leakInfo = leakInfo.substring(0, LOG_LIMIT);
        }

        Crashlytics.log("*** Memory Leak ***");
        for (final String s : leakInfo.split("\n")) {
            Crashlytics.log(s);
        }

        String name = classSimpleName(result.className);
        if (!heapDump.referenceName.equals("")) {
            name += "(" + heapDump.referenceName + ")";
        }
        Crashlytics.logException(result.leakTraceAsFakeException());
    }
}
