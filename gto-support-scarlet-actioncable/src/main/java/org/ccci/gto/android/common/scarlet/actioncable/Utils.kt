package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter

internal fun <T> MessageAdapter<T>.fromMessage(msg: String) = fromMessage(Message.Text(msg))
