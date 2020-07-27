package org.ccci.gto.android.common.scarlet.actioncable

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tinder.scarlet.Message as ScarletMessage
import com.tinder.scarlet.MessageAdapter
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.scarlet.actioncable.model.ConfirmSubscription
import org.ccci.gto.android.common.scarlet.actioncable.model.Message
import org.ccci.gto.android.common.scarlet.actioncable.model.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class ActionCableMessageAdapterTest : BaseActionCableMessageAdapterTest() {
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

    @Test(expected = IllegalArgumentException::class)
    fun verifySubscribeMessageAdapterFromMessageInvalidCommand() {
        val rawMessage = """{"identifier":"{\"channel\":\"channel\"}","command":"invalid"}"""
        val adapter = factory.create(Subscribe::class.java, emptyArray()) as MessageAdapter<Subscribe>

        adapter.fromMessage(ScarletMessage.Text(rawMessage))
        fail("adapter.fromMessage() should have thrown an error because the command is invalid")
    }
    // endregion Subscribe MessageAdapter

    // region ConfirmSubscription MessageAdapter
    @Test
    fun verifyConfirmSubscriptionMessageAdapterToMessage() {
        val adapter =
            factory.create(ConfirmSubscription::class.java, emptyArray()) as MessageAdapter<ConfirmSubscription>
        val message = adapter.toMessage(ConfirmSubscription("channel"))
        assertThat(message, instanceOf(ScarletMessage.Text::class.java))

        val json = (message as ScarletMessage.Text).value
        assertThatJson(json).node("type").isEqualTo("confirm_subscription")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("{channel:\"channel\"}")
    }

    @Test
    fun verifyConfirmSubscriptionMessageAdapterFromMessage() {
        val rawMessage = """{"identifier":"{\"channel\":\"channel\"}","type":"confirm_subscription"}"""
        val adapter =
            factory.create(ConfirmSubscription::class.java, emptyArray()) as MessageAdapter<ConfirmSubscription>

        val confirm = adapter.fromMessage(ScarletMessage.Text(rawMessage))
        assertEquals("confirm_subscription", confirm.type)
        assertEquals("channel", confirm.identifier.channel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyConfirmSubscriptionMessageAdapterFromMessageInvalidType() {
        val rawMessage = """{"identifier":"{\"channel\":\"channel\"}","type":"other_type"}"""
        val adapter =
            factory.create(ConfirmSubscription::class.java, emptyArray()) as MessageAdapter<ConfirmSubscription>

        adapter.fromMessage(ScarletMessage.Text(rawMessage))
        fail("adapter.fromMessage() should have thrown an error because the type is invalid")
    }
    // endregion ConfirmSubscription MessageAdapter

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
    // endregion Message MessageAdapter

    @Test(expected = IllegalStateException::class)
    fun verifyDataMessageAdapterExceptionIfMissingAnnotation() {
        try {
            factory.create(String::class.java, emptyArray())
        } finally {
            verify(dataMessageAdapterFactory, never()).create(any(), any())
        }
    }
}
