package org.ccci.gto.android.common.napier

import android.util.Log
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import timber.log.Timber

object TimberAntilog : Antilog() {
    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) =
        Timber.apply { tag?.let { tag(it) } }.log(priority.toValue(), message, throwable)

    private fun LogLevel.toValue() = when (this) {
        LogLevel.VERBOSE -> Log.VERBOSE
        LogLevel.DEBUG -> Log.DEBUG
        LogLevel.INFO -> Log.INFO
        LogLevel.WARNING -> Log.WARN
        LogLevel.ERROR -> Log.ERROR
        LogLevel.ASSERT -> Log.ASSERT
    }
}
