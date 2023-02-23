package org.ccci.gto.android.common.androidx.work

import android.annotation.SuppressLint
import android.util.Log
import androidx.work.Logger
import timber.log.Timber

@SuppressLint("RestrictedApi")
class TimberLogger(private val loggingLevel: Int) : Logger(loggingLevel) {
    override fun verbose(tag: String, message: String) = log(Log.VERBOSE, tag, message)
    override fun verbose(tag: String, message: String, throwable: Throwable) = log(Log.VERBOSE, tag, message, throwable)
    override fun debug(tag: String, message: String) = log(Log.DEBUG, tag, message)
    override fun debug(tag: String, message: String, throwable: Throwable) = log(Log.DEBUG, tag, message, throwable)
    override fun info(tag: String, message: String) = log(Log.INFO, tag, message)
    override fun info(tag: String, message: String, throwable: Throwable) = log(Log.INFO, tag, message, throwable)
    override fun warning(tag: String, message: String) = log(Log.WARN, tag, message)
    override fun warning(tag: String, message: String, throwable: Throwable) = log(Log.WARN, tag, message, throwable)
    override fun error(tag: String, message: String) = log(Log.ERROR, tag, message)
    override fun error(tag: String, message: String, throwable: Throwable) = log(Log.ERROR, tag, message, throwable)

    private fun log(priority: Int, tag: String, message: String, throwable: Throwable? = null) {
        if (loggingLevel > priority) return

        when (throwable) {
            null -> Timber.tag(tag).log(priority, message)
            else -> Timber.tag(tag).log(priority, throwable, message)
        }
    }

    fun install() = setLogger(this)
}
