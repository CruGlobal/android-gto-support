package org.ccci.gto.android.common.crashlytics.timber

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree @JvmOverloads constructor(
    private val logLevel: Int,
    private val exceptionLogLevel: Int = logLevel
) : Timber.Tree() {
    constructor() : this(Log.INFO, Log.ERROR)

    override fun isLoggable(tag: String?, priority: Int) = logLevel <= priority
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.log(priority, tag, message)
        if (t != null && exceptionLogLevel <= priority) Crashlytics.logException(t)
    }
}
