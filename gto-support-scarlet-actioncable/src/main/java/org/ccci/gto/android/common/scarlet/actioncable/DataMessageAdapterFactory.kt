package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier
import org.ccci.gto.android.common.scarlet.actioncable.model.RawMessage
import org.ccci.gto.android.common.scarlet.stringValue
import com.tinder.scarlet.Message as ScarletMessage

internal class DataMessageAdapterFactory<T>(
    private val identifier: Identifier,
    private val rawMessageAdapter: MessageAdapter<RawMessage>,
    private val dataAdapter: MessageAdapter<T>
) : MessageAdapter<T> {
    override fun fromMessage(message: ScarletMessage) = rawMessageAdapter.fromMessage(message)
        .also { check(it.identifier == identifier) { "This message is for a different channel" } }
        .data.let { dataAdapter.fromMessage(it) }

    override fun toMessage(data: T) =
        rawMessageAdapter.toMessage(RawMessage(identifier, dataAdapter.toMessage(data).stringValue))
}
