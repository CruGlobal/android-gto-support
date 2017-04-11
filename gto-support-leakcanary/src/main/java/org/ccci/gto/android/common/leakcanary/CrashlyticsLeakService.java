package org.ccci.gto.android.common.leakcanary;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.HeapDump;

public class CrashlyticsLeakService extends DisplayLeakService {
    @NonNull
    private static String classSimpleName(@NonNull final String className) {
        int separator = className.lastIndexOf('.');
        return separator == -1 ? className : className.substring(separator + 1);
    }

    protected void afterDefaultHandling(@NonNull final HeapDump heapDump, @NonNull final AnalysisResult result,
                                        @NonNull final String leakInfo) {
        if (!result.leakFound || result.excludedLeak) {
            return;
        }
        Crashlytics.log("*** Memory Leak ***");
        for (final String s : leakInfo.split("\n")) {
            Crashlytics.log(s);
        }
        Crashlytics.log("*** End Of Leak ***");

        String name = classSimpleName(result.className);
        if (!heapDump.referenceName.equals("")) {
            name += "(" + heapDump.referenceName + ")";
        }
        Crashlytics.logException(new Exception(name + " has leaked"));
    }
}
