package org.ccci.gto.android.common.eventbus

import org.greenrobot.eventbus.Logger
import timber.log.Timber
import java.util.logging.Level

private const val TAG = "EventBus"

class TimberLogger : Logger.AndroidLogger(TAG) {
    override fun log(level: Level, msg: String?) = Timber.tag(TAG).log(mapLevel(level), msg)
    override fun log(level: Level, msg: String?, th: Throwable?) = Timber.tag(TAG).log(mapLevel(level), th, msg)
}
