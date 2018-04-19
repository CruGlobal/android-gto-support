package org.ccci.gto.android.common.crashlytics.timber;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public final class CrashlyticsTree extends Timber.Tree {
    @Override
    protected boolean isLoggable(@Nullable final String tag, final int priority) {
        // defer to the Fabric logger to determine if this message is loggable
        return Fabric.getLogger().isLoggable(tag, priority);
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
