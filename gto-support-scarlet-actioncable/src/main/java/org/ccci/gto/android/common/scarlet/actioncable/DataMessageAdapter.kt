package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier
import org.ccci.gto.android.common.scarlet.actioncable.model.RawMessage
import org.ccci.gto.android.common.scarlet.stringValue
import com.tinder.scarlet.Message as ScarletMessage

internal class DataMessageAdapter<T>(
    private val rawMessageAdapter: MessageAdapter<RawMessage>,
    private val dataAdapter: MessageAdapter<T>,
    annotations: Array<Annotation>
) : MessageAdapter<T> {
    private val actionCableMessage = annotations.actionCableMessage
        ?: error("ActionCableMessage annotation is required for this MessageAdapter")
    private val identifier = Identifier(actionCableMessage.channel)

    override fun fromMessage(message: ScarletMessage) = rawMessageAdapter.fromMessage(message)
        .also { it.identifier.require(actionCableMessage = actionCableMessage) }
        .data.let { dataAdapter.fromMessage(it) }

    override fun toMessage(data: T) =
        rawMessageAdapter.toMessage(RawMessage(identifier, dataAdapter.toMessage(data).stringValue))
}
