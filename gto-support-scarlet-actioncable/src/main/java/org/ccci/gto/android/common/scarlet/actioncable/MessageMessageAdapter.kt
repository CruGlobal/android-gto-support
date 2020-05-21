package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.IncomingMessage
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.OutgoingMessage
import org.ccci.gto.android.common.scarlet.stringValue
import com.tinder.scarlet.Message as ScarletMessage

internal class MessageMessageAdapter<T>(
    private val incomingMessageAdapter: MessageAdapter<IncomingMessage>,
    private val outgoingMessageAdapter: MessageAdapter<OutgoingMessage>,
    private val dataAdapter: MessageAdapter<T>,
    annotations: Array<Annotation> = emptyArray()
) : MessageAdapter<Message<T>> {
    private val actionCableChannel = annotations.actionCableChannel

    override fun fromMessage(message: ScarletMessage) =
        incomingMessageAdapter.fromMessage(message).let {
            it.identifier.require(actionCableChannel)
            Message(it.identifier, dataAdapter.fromMessage(it.message))
        }

    override fun toMessage(data: Message<T>): ScarletMessage {
        data.identifier.require(actionCableChannel)
        return outgoingMessageAdapter.toMessage(
            OutgoingMessage(data.identifier, dataAdapter.toMessage(data.data).stringValue)
        )
    }
}
