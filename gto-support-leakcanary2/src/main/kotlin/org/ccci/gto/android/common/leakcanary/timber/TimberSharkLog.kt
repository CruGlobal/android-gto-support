package org.ccci.gto.android.common.leakcanary.timber

import shark.SharkLog
import timber.log.Timber

private const val TAG = "LeakCanary"

private const val MAX_MESSAGE_LENGTH = 4000
private val REGEX_NEW_LINE = "\n".toRegex()

object TimberSharkLog : SharkLog.Logger {
    override fun d(message: String) {
        if (message.length >= MAX_MESSAGE_LENGTH) {
            message.split(REGEX_NEW_LINE).forEach { Timber.tag(TAG).d(it) }
        } else {
            Timber.tag(TAG).d(message)
        }
    }

    override fun d(throwable: Throwable, message: String) {
        if (message.length >= MAX_MESSAGE_LENGTH) {
            d(message)
            Timber.tag(TAG).d(throwable)
        } else {
            Timber.tag(TAG).d(throwable, message)
        }
    }
}
