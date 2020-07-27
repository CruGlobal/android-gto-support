package org.ccci.gto.android.common.scarlet.actioncable

import com.squareup.moshi.Moshi
import com.tinder.scarlet.Message as ScarletMessage
import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.RawIncomingMessage
import org.ccci.gto.android.common.scarlet.actioncable.model.RawOutgoingMessage
import org.ccci.gto.android.common.scarlet.stringValue

internal class MessageMessageAdapter<T>(
    private val dataAdapter: MessageAdapter<T>,
    moshi: Moshi,
    annotations: Array<Annotation> = emptyArray()
) : MessageAdapter<Message<T>> {
    private val anyAdapter = moshi.adapter(Any::class.java)
    private val incomingMessageAdapter = moshi.adapter(RawIncomingMessage::class.java)
    private val outgoingMessageAdapter = moshi.adapter(RawOutgoingMessage::class.java)

    private val actionCableChannel = annotations.actionCableChannel
    private val serializeDataAsJson = annotations.actionCableSerializeDataAsJson != null

    override fun fromMessage(message: ScarletMessage) =
        incomingMessageAdapter.fromJson(message.stringValue)!!.let {
            it.identifier.require(actionCableChannel)
            Message(
                it.identifier,
                dataAdapter.fromMessage(
                    when (it.message) {
                        is String -> it.message
                        else -> anyAdapter.toJson(it.message)
                    }
                )
            )
        }

    override fun toMessage(data: Message<T>): ScarletMessage {
        data.identifier.require(actionCableChannel)
        val payload = dataAdapter.toMessage(data.data).stringValue
            .let { if (serializeDataAsJson) anyAdapter.fromJson(it)!! else it }
        return ScarletMessage.Text(outgoingMessageAdapter.toJson(RawOutgoingMessage(data.identifier, payload)))
    }
}
