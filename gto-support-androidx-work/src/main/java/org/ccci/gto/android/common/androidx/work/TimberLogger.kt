package org.ccci.gto.android.common.androidx.work

import android.annotation.SuppressLint
import android.util.Log
import androidx.work.Logger
import timber.log.Timber

@SuppressLint("RestrictedApi")
class TimberLogger(private val loggingLevel: Int) : Logger(loggingLevel) {
    override fun verbose(tag: String?, message: String?, vararg throwables: Throwable?) =
        log(Log.VERBOSE, tag, message, *throwables)

    override fun debug(tag: String?, message: String?, vararg throwables: Throwable?) =
        log(Log.DEBUG, tag, message, *throwables)

    override fun info(tag: String?, message: String?, vararg throwables: Throwable?) =
        log(Log.INFO, tag, message, *throwables)

    override fun warning(tag: String?, message: String?, vararg throwables: Throwable?) =
        log(Log.WARN, tag, message, *throwables)

    override fun error(tag: String?, message: String?, vararg throwables: Throwable?) =
        log(Log.ERROR, tag, message, *throwables)

    private fun log(priority: Int, tag: String?, message: String?, vararg throwables: Throwable?) {
        if (loggingLevel > priority) return

        val throwablesToLog = throwables.filterNotNull()
        when {
            throwablesToLog.isEmpty() -> Timber.tag(tag).log(priority, message)
            else -> {
                Timber.tag(tag).log(priority, throwablesToLog.first(), message)
                if (throwablesToLog.size > 1) throwablesToLog.drop(1).forEach { Timber.tag(tag).log(priority, it) }
            }
        }
    }

    fun install() = setLogger(this)
}

private inline fun Timber.Forest.tag(tag: String?) = apply { if (tag != null) tag(tag) }
