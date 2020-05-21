package org.ccci.gto.android.common.scarlet.actioncable

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tinder.scarlet.MessageAdapter
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.tinder.scarlet.Message as ScarletMessage

@Suppress("UNCHECKED_CAST")
class ActionCableMessageAdapterTest {
    private lateinit var factory: MessageAdapter.Factory

    private lateinit var dataMessageAdapterFactory: MessageAdapter.Factory
    private lateinit var dataMessageAdapter: MessageAdapter<Any>

    private val actionCableAnnotation by lazy {
        Annotations::class.java.getDeclaredMethod("actionCable").annotations
            .filterIsInstance(ActionCableMessage::class.java).first()
    }

    @Before
    fun setup() {
        dataMessageAdapter = mock()
        dataMessageAdapterFactory = mock()
        whenever(dataMessageAdapterFactory.create(any(), any())).thenReturn(dataMessageAdapter)

        factory = ActionCableMessageAdapterFactory.Builder()
            .addMessageAdapterFactory(dataMessageAdapterFactory)
            .build()
    }

    // region Subscribe MessageAdapter
    @Test
    fun verifySubscribeMessageAdapterToMessage() {
        val adapter = factory.create(Subscribe::class.java, emptyArray()) as MessageAdapter<Subscribe>
        val message = adapter.toMessage(Subscribe("channel"))
        assertThat(message, instanceOf(ScarletMessage.Text::class.java))

        val json = (message as ScarletMessage.Text).value
        assertThatJson(json).node("command").isEqualTo("subscribe")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("{channel:\"channel\"}")
    }

    @Test
    fun verifySubscribeMessageAdapterFromMessage() {
        val rawMessage = """{"identifier":"{\"channel\":\"channel\"}","command":"subscribe"}"""
        val adapter = factory.create(Subscribe::class.java, emptyArray()) as MessageAdapter<Subscribe>

        val subscribe = adapter.fromMessage(ScarletMessage.Text(rawMessage))
        assertEquals("subscribe", subscribe.command)
        assertEquals("channel", subscribe.identifier.channel)
    }
    // endregion Subscribe MessageAdapter

    // region Message MessageAdapter
    @Test(expected = IllegalArgumentException::class)
    fun verifyMessageMessageAdapterExceptionIfNotParameterized() {
        try {
            factory.create(genericReturnTypeOf<MessageParameterizedTypes>("notParameterized"), emptyArray())
        } finally {
            verify(dataMessageAdapterFactory, never()).create(any(), any())
        }
    }

    @Test(expected = IllegalStateException::class)
    fun verifyMessageMessageAdapterExceptionIfUnsupportedParameterizedType() {
        whenever(dataMessageAdapterFactory.create(eq(CharSequence::class.java), any()))
            .thenThrow(RuntimeException::class.java)

        try {
            factory.create(genericReturnTypeOf<MessageParameterizedTypes>("charSequenceParameterized"), emptyArray())
        } finally {
            verify(dataMessageAdapterFactory).create(eq(CharSequence::class.java), any())
        }
    }

    @Test
    fun verifyMessageMessageAdapterFromMessage() {
        val rawMessage = """{"identifier":"{\"channel\":\"c\"}","command":"message","message":"raw"}"""
        whenever(dataMessageAdapter.fromMessage(any())).thenReturn("response")
        val adapter =
            factory.create(genericReturnTypeOf<MessageParameterizedTypes>("charSequenceParameterized"), emptyArray())

        val msg = adapter.fromMessage(ScarletMessage.Text(rawMessage)) as Message<CharSequence>
        assertEquals("c", msg.identifier.channel)
        assertEquals("response", msg.data)
        verify(dataMessageAdapter).fromMessage(eq(ScarletMessage.Text("raw")))
    }

    @Test
    fun verifyMessageMessageAdapterToMessage() {
        val data = Message<CharSequence>("c", "data")
        whenever(dataMessageAdapter.toMessage(eq("data"))).thenReturn(ScarletMessage.Text("raw"))
        val adapter = factory.create(
            genericReturnTypeOf<MessageParameterizedTypes>("charSequenceParameterized"), emptyArray()
        ) as MessageAdapter<Message<CharSequence>>

        val json = (adapter.toMessage(data) as ScarletMessage.Text).value
        assertThatJson(json).node("command").isEqualTo("message")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("""{channel:"c"}""")
        assertThatJson(json).node("data").isEqualTo("raw")
        verify(dataMessageAdapter).toMessage(eq("data"))
    }

    private interface MessageParameterizedTypes {
        fun notParameterized(): Message<*>
        fun charSequenceParameterized(): Message<CharSequence>
    }
    // endregion Message MessageAdapter

    // region Data MessageAdapter
    @Test(expected = IllegalStateException::class)
    fun verifyDataMessageAdapterExceptionIfMissingAnnotation() {
        try {
            factory.create(String::class.java, emptyArray())
        } finally {
            verify(dataMessageAdapterFactory, never()).create(any(), any())
        }
    }

    @Test
    fun verifyDataMessageAdapterToMessage() {
        whenever(dataMessageAdapter.toMessage(any()))
            .thenAnswer { ScarletMessage.Text((it.arguments.first() as String).reversed()) }
        val adapter =
            factory.create(CharSequence::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<CharSequence>

        val json = (adapter.toMessage("data") as ScarletMessage.Text).value
        assertThatJson(json).node("command").isEqualTo("message")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("""{channel:"valid"}""")
        assertThatJson(json).node("data").isEqualTo("atad")
        verify(dataMessageAdapter).toMessage(eq("data"))
    }

    @Test
    fun verifyDataMessageAdapterFromMessageValidChannel() {
        val rawMessage = """{"identifier":"{\"channel\":\"valid\"}","command":"message","message":"data"}"""
        whenever(dataMessageAdapter.fromMessage(any()))
            .thenAnswer { (it.arguments.first() as ScarletMessage.Text).value.reversed() }
        val adapter =
            factory.create(CharSequence::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<CharSequence>

        val data = adapter.fromMessage(ScarletMessage.Text(rawMessage))
        assertEquals("atad", data)
        verify(dataMessageAdapter).fromMessage(eq(ScarletMessage.Text("data")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyDataMessageAdapterFromMessageInvalidChannel() {
        val rawMessage = """{"identifier":"{\"channel\":\"invalid\"}","command":"message","message":"data"}"""
        whenever(dataMessageAdapter.fromMessage(any()))
            .thenAnswer { (it.arguments.first() as ScarletMessage.Text).value.reversed() }
        val adapter = factory.create(String::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<String>

        try {
            adapter.fromMessage(ScarletMessage.Text(rawMessage))
        } finally {
            verify(dataMessageAdapter, never()).fromMessage(any())
        }
    }
    // endregion Data MessageAdapter

    private inline fun <reified T : Any> genericReturnTypeOf(method: String) =
        T::class.java.getDeclaredMethod(method).genericReturnType
}

private interface Annotations {
    @ActionCableMessage("valid")
    fun actionCable(): String
}
