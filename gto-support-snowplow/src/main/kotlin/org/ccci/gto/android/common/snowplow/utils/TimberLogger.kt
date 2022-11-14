package org.ccci.gto.android.common.snowplow.utils

import com.snowplowanalytics.snowplow.tracker.LoggerDelegate
import timber.log.Timber

@Deprecated("Since v3.14.0, We no longer use snowplow for any analytics")
object TimberLogger : LoggerDelegate {
    override fun error(tag: String, msg: String) = Timber.tag(tag).e(msg)
    override fun debug(tag: String, msg: String) = Timber.tag(tag).d(msg)
    override fun verbose(tag: String, msg: String) = Timber.tag(tag).v(msg)
}
