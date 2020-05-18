package org.ccci.gto.android.common.scarlet.actioncable

import com.squareup.moshi.Moshi
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.messageadapter.builtin.BuiltInMessageAdapterFactory
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.utils.getParameterUpperBound
import com.tinder.scarlet.utils.getRawType
import com.tinder.scarlet.utils.hasUnresolvableType
import org.ccci.gto.android.common.moshi.adapter.StringifyJsonAdapterFactory
import org.ccci.gto.android.common.scarlet.actioncable.model.Identifier
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.RawMessage
import org.ccci.gto.android.common.scarlet.actioncable.model.Subscribe
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ActionCableMessageAdapterFactory private constructor(
    private val messageAdapterFactories: List<MessageAdapter.Factory>
) : MessageAdapter.Factory {
    private val moshi = Moshi.Builder().add(StringifyJsonAdapterFactory).build()
    private val moshiMessageAdapterFactory = MoshiMessageAdapter.Factory(moshi)
    private val rawMessageAdapter by lazy {
        @Suppress("UNCHECKED_CAST")
        moshiMessageAdapterFactory.create(RawMessage::class.java, emptyArray()) as MessageAdapter<RawMessage>
    }

    override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> = when (type.getRawType()) {
        Subscribe::class.java -> moshiMessageAdapterFactory.create(type, annotations)
        Message::class.java -> {
            require(type is ParameterizedType && !type.hasUnresolvableType()) {
                "ActionCable Message type requires a resolvable ParameterizedType"
            }
            MessageMessageAdapterFactory(
                rawMessageAdapter, findMessageAdapter(type.getParameterUpperBound(0), annotations)
            )
        }
        else -> {
            val actionCableMessage = annotations.firstOrNull { it is ActionCableMessage } as? ActionCableMessage
                ?: error("Type is not supported by this MessageAdapterFactory: $type")
            DataMessageAdapterFactory(
                Identifier(actionCableMessage.channel), rawMessageAdapter,
                findMessageAdapter(type, annotations.filterNot { it is ActionCableMessage }.toTypedArray())
            )
        }
    }

    private fun findMessageAdapter(type: Type, annotations: Array<Annotation>): MessageAdapter<Any> {
        messageAdapterFactories.forEach {
            try {
                @Suppress("UNCHECKED_CAST")
                return it.create(type, annotations) as MessageAdapter<Any>
            } catch (e: Throwable) {
            }
        }
        error("Cannot resolve message adapter for type: $type, annotations: $annotations.")
    }

    class Builder {
        private val messageAdapterFactories = mutableListOf<MessageAdapter.Factory>(BuiltInMessageAdapterFactory())
        fun addMessageAdapterFactory(factory: MessageAdapter.Factory) = apply { messageAdapterFactories.add(factory) }
        fun build() = ActionCableMessageAdapterFactory(messageAdapterFactories)
    }
}
