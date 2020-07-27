package org.ccci.gto.android.common.scarlet.actioncable

import com.squareup.moshi.Moshi
import com.tinder.scarlet.Message as ScarletMessage
import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier
import org.ccci.gto.android.common.scarlet.actioncable.model.RawIncomingMessage
import org.ccci.gto.android.common.scarlet.actioncable.model.RawOutgoingMessage
import org.ccci.gto.android.common.scarlet.stringValue

internal class DataMessageAdapter<T>(
    private val dataAdapter: MessageAdapter<T>,
    moshi: Moshi,
    annotations: Array<Annotation>
) : MessageAdapter<T> {
    private val anyAdapter = moshi.adapter(Any::class.java)
    private val incomingMessageAdapter = moshi.adapter(RawIncomingMessage::class.java)
    private val outgoingMessageAdapter = moshi.adapter(RawOutgoingMessage::class.java)

    private val actionCableMessage = annotations.actionCableMessage
        ?: error("ActionCableMessage annotation is required for this MessageAdapter")
    private val serializeDataAsJson = annotations.actionCableSerializeDataAsJson != null
    private val identifier = Identifier(actionCableMessage.channel)

    override fun fromMessage(message: ScarletMessage) =
        incomingMessageAdapter.fromJson(message.stringValue)!!
            .also { it.identifier.require(actionCableMessage = actionCableMessage) }
            .message.let { if (it is String) it else anyAdapter.toJson(it) }
            .let { dataAdapter.fromMessage(it) }

    override fun toMessage(data: T) = dataAdapter.toMessage(data).stringValue
        .let { if (serializeDataAsJson) anyAdapter.fromJson(it)!! else it }
        .let { ScarletMessage.Text(outgoingMessageAdapter.toJson(RawOutgoingMessage(identifier, it))) }
}
