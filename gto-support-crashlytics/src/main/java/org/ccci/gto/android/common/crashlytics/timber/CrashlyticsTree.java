package org.ccci.gto.android.common.crashlytics.timber;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public final class CrashlyticsTree extends Timber.Tree {
    private int mLogLevel;

    public CrashlyticsTree() {
        mLogLevel = Log.INFO;
    }

    public CrashlyticsTree(final int logLevel) {
        mLogLevel = logLevel;
    }

    @Override
    protected boolean isLoggable(@Nullable final String tag, final int priority) {
        return mLogLevel <= priority;
    }

    @Override
    protected void log(final int priority, @Nullable final String tag, @NotNull final String message,
                       @Nullable final Throwable t) {
        Crashlytics.log(priority, tag, message);

        if (t != null) {
            Crashlytics.logException(t);
        }
    }
}
