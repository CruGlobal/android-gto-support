package org.ccci.gto.android.common.newrelic;

import android.content.Context;
import android.support.annotation.NonNull;

import com.newrelic.agent.android.crashes.CrashStore;
import com.newrelic.agent.android.harvest.crash.Crash;
import com.newrelic.agent.android.util.JsonCrashStore;

public class CrashReporterUtils {
    private static CrashStore nrCrashStore = null;

    public static void reportException(@NonNull final Context context, @NonNull final Throwable t) {
        CrashStore store = nrCrashStore;
        if (store == null) {
            store = new JsonCrashStore(context.getApplicationContext());
            nrCrashStore = store;
        }
        store.store(new Crash(t));
    }
}
