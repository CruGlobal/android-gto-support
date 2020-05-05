package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.RawMessage
import com.tinder.scarlet.Message as ScarletMessage

internal class MessageMessageAdapterFactory<T>(
    private val rawMessageAdapter: MessageAdapter<RawMessage>,
    private val dataAdapter: MessageAdapter<T>
) : MessageAdapter<Message<T>> {
    override fun fromMessage(message: ScarletMessage) = rawMessageAdapter.fromMessage(message).adaptData(dataAdapter)
    override fun toMessage(data: Message<T>) = TODO("Not yet implemented")

    private fun <T> RawMessage.adaptData(dataAdapter: MessageAdapter<T>) =
        Message(identifier, data?.let { dataAdapter.fromMessage(ScarletMessage.Text(it)) })
}
