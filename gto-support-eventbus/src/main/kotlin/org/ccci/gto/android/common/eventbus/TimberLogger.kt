package org.ccci.gto.android.common.eventbus

import android.util.Log
import androidx.annotation.VisibleForTesting
import java.util.logging.Level
import org.greenrobot.eventbus.Logger
import timber.log.Timber

private const val TAG = "EventBus"

object TimberLogger : Logger {
    override fun log(level: Level, msg: String?) = Timber.tag(TAG).log(mapLevel(level), msg)
    override fun log(level: Level, msg: String?, th: Throwable?) = Timber.tag(TAG).log(mapLevel(level), th, msg)

    @VisibleForTesting
    internal fun mapLevel(level: Level): Int {
        val value = level.intValue()
        return when {
            value >= Level.SEVERE.intValue() -> Log.ERROR
            value >= Level.WARNING.intValue() -> Log.WARN
            value >= Level.INFO.intValue() -> Log.INFO
            value >= Level.CONFIG.intValue() -> Log.DEBUG
            else -> Log.VERBOSE
        }
    }
}
