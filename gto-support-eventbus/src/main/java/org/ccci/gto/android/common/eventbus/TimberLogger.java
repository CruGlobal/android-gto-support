package org.ccci.gto.android.common.eventbus;

import org.greenrobot.eventbus.Logger;

import java.util.logging.Level;

import timber.log.Timber;

public final class TimberLogger extends Logger.AndroidLogger {
    private static final String TAG = "EventBus";

    public TimberLogger() {
        super(TAG);
    }

    @Override
    public void log(final Level level, final String msg) {
        Timber.tag(TAG).log(mapLevel(level), msg);
    }

    @Override
    public void log(final Level level, final String msg, final Throwable th) {
        Timber.tag(TAG).log(mapLevel(level), th, msg);
    }
}
