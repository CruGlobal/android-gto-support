package org.ccci.gto.android.common.firebase.crashlytics.timber

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

import timber.log.Timber

class CrashlyticsTree @JvmOverloads constructor(
    private val logLevel: Int,
    private val exceptionLogLevel: Int = logLevel
) : Timber.Tree() {
    constructor() : this(Log.INFO, Log.ERROR)

    private val crashlytics get() = FirebaseCrashlytics.getInstance()

    override fun isLoggable(tag: String?, priority: Int) = logLevel <= priority
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        with(crashlytics) {
            log(message)
            if (t != null && exceptionLogLevel <= priority) recordException(t)
        }
    }
}
