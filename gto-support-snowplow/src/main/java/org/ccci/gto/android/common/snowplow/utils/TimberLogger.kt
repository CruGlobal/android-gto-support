package org.ccci.gto.android.common.snowplow.utils

import com.snowplowanalytics.snowplow.tracker.DiagnosticLogger
import com.snowplowanalytics.snowplow.tracker.LoggerDelegate
import timber.log.Timber

object TimberLogger : LoggerDelegate, DiagnosticLogger {
    override fun error(tag: String, msg: String) = Timber.tag(tag).e(msg)
    override fun debug(tag: String, msg: String) = Timber.tag(tag).d(msg)
    override fun verbose(tag: String, msg: String) = Timber.tag(tag).v(msg)

    override fun log(tag: String, msg: String, t: Throwable?) {
        if (t != null) Timber.tag(tag).e(t, msg)
    }
}
