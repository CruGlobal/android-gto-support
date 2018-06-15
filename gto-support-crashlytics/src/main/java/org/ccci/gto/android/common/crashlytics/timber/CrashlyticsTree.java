package org.ccci.gto.android.common.crashlytics.timber;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public final class CrashlyticsTree extends Timber.Tree {
    private int mLogLevel;
    private int mExceptionLogLevel;

    public CrashlyticsTree() {
        this(Log.INFO, Log.ERROR);
    }

    public CrashlyticsTree(final int logLevel) {
        this(logLevel, logLevel);
    }

    public CrashlyticsTree(final int logLevel, final int exceptionLogLevel) {
        mLogLevel = logLevel;
        mExceptionLogLevel = exceptionLogLevel;
    }

    @Override
    protected boolean isLoggable(@Nullable final String tag, final int priority) {
        return mLogLevel <= priority;
    }

    @Override
    protected void log(final int priority, @Nullable final String tag, @NotNull final String message,
                       @Nullable final Throwable t) {
        Crashlytics.log(priority, tag, message);

        if (t != null && mExceptionLogLevel <= priority) {
            Crashlytics.logException(t);
        }
    }
}
