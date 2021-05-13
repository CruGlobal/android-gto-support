package org.ccci.gto.android.common.snowplow.utils

import com.snowplowanalytics.snowplow.tracker.LoggerDelegate
import timber.log.Timber

object TimberLogger : LoggerDelegate {
    override fun error(tag: String, msg: String) = Timber.tag(tag).e(msg)
    override fun debug(tag: String, msg: String) = Timber.tag(tag).d(msg)
    override fun verbose(tag: String, msg: String) = Timber.tag(tag).v(msg)
}
