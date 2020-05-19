package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.RawMessage
import org.ccci.gto.android.common.scarlet.stringValue
import com.tinder.scarlet.Message as ScarletMessage

internal class MessageMessageAdapter<T>(
    private val rawMessageAdapter: MessageAdapter<RawMessage>,
    private val dataAdapter: MessageAdapter<T>,
    annotations: Array<Annotation> = emptyArray()
) : MessageAdapter<Message<T>> {
    private val actionCableChannel = annotations.actionCableChannel

    override fun fromMessage(message: ScarletMessage) =
        with(rawMessageAdapter.fromMessage(message)) {
            identifier.require(actionCableChannel)
            Message(identifier, dataAdapter.fromMessage(data))
        }

    override fun toMessage(data: Message<T>): ScarletMessage {
        data.identifier.require(actionCableChannel)
        return rawMessageAdapter.toMessage(RawMessage(data.identifier, dataAdapter.toMessage(data.data).stringValue))
    }
}
