package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier

internal fun <T> MessageAdapter<T>.fromMessage(msg: String) = fromMessage(Message.Text(msg))

private const val MSG_WRONG_CHANNEL = "This message is for a different channel"

internal fun Identifier.require(
    actionCableChannel: ActionCableChannel? = null,
    actionCableMessage: ActionCableMessage? = null
) {
    if (actionCableChannel != null) require(channel == actionCableChannel.channel) { MSG_WRONG_CHANNEL }
    if (actionCableMessage != null) require(channel == actionCableMessage.channel) { MSG_WRONG_CHANNEL }
}
