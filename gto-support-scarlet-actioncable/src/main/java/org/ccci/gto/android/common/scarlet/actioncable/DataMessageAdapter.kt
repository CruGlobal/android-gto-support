package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier
import org.ccci.gto.android.common.scarlet.actioncable.model.IncomingMessage
import org.ccci.gto.android.common.scarlet.actioncable.model.OutgoingMessage
import org.ccci.gto.android.common.scarlet.stringValue
import com.tinder.scarlet.Message as ScarletMessage

internal class DataMessageAdapter<T>(
    private val incomingMessageAdapter: MessageAdapter<IncomingMessage>,
    private val outgoingMessageAdapter: MessageAdapter<OutgoingMessage>,
    private val dataAdapter: MessageAdapter<T>,
    annotations: Array<Annotation>
) : MessageAdapter<T> {
    private val actionCableMessage = annotations.actionCableMessage
        ?: error("ActionCableMessage annotation is required for this MessageAdapter")
    private val identifier = Identifier(actionCableMessage.channel)

    override fun fromMessage(message: ScarletMessage) = incomingMessageAdapter.fromMessage(message)
        .also { it.identifier.require(actionCableMessage = actionCableMessage) }
        .message.let { dataAdapter.fromMessage(it) }

    override fun toMessage(data: T) =
        outgoingMessageAdapter.toMessage(OutgoingMessage(identifier, dataAdapter.toMessage(data).stringValue))
}
