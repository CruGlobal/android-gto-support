package org.ccci.gto.android.common.kermit

import android.util.Log
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import timber.log.Timber

object TimberLogWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) =
        Timber.tag(tag).log(severity.toValue(), throwable, message)

    private fun Severity.toValue() = when (this) {
        Severity.Verbose -> Log.VERBOSE
        Severity.Debug -> Log.DEBUG
        Severity.Info -> Log.INFO
        Severity.Warn -> Log.WARN
        Severity.Error -> Log.ERROR
        Severity.Assert -> Log.ASSERT
    }
}
