package org.ccci.gto.android.common.scarlet.actioncable

import com.squareup.moshi.JsonEncodingException
import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@Suppress("UNCHECKED_CAST")
@RunWith(JUnitParamsRunner::class)
class ActionCableDataMessageAdapterTest : BaseActionCableMessageAdapterTest() {
    // region fromMessage()
    @Test
    fun verifyFromMessageSimpleMessage() {
        val rawMessage = """{"identifier":"{\"channel\":\"valid\"}","command":"message","message":"payload"}"""
        val output = 5
        whenever(dataMessageAdapter.fromMessage(eq(Message.Text("payload")))).thenReturn(output)
        val adapter = factory.create(Int::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<Int>

        val data = adapter.fromMessage(Message.Text(rawMessage))
        Assert.assertEquals(output, data)
        verify(dataMessageAdapter).fromMessage(eq(Message.Text("payload")))
    }

    @Test
    @Parameters("""{"attr":"value"}""", """"{\"attr\":\"value\"}"""")
    fun verifyFromMessageComplexMessage(payload: String) {
        val rawMessage = """{"identifier":"{\"channel\":\"valid\"}","command":"message","message":$payload}"""
        val output = Any()
        whenever(dataMessageAdapter.fromMessage(eq(Message.Text("""{"attr":"value"}""")))).thenReturn(output)
        val adapter = factory.create(Any::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<Any>

        val data = adapter.fromMessage(Message.Text(rawMessage))
        Assert.assertSame(output, data)
        verify(dataMessageAdapter).fromMessage(eq(Message.Text("""{"attr":"value"}""")))
    }

    @Test
    fun verifyFromMessageValidChannel() {
        val rawMessage = """{"identifier":"{\"channel\":\"valid\"}","command":"message","message":"data"}"""
        whenever(dataMessageAdapter.fromMessage(any()))
            .thenAnswer { (it.arguments.first() as Message.Text).value.reversed() }
        val adapter =
            factory.create(CharSequence::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<CharSequence>

        val data = adapter.fromMessage(Message.Text(rawMessage))
        Assert.assertEquals("atad", data)
        verify(dataMessageAdapter).fromMessage(eq(Message.Text("data")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyFromMessageInvalidChannel() {
        val rawMessage = """{"identifier":"{\"channel\":\"invalid\"}","command":"message","message":"data"}"""
        whenever(dataMessageAdapter.fromMessage(any()))
            .thenAnswer { (it.arguments.first() as Message.Text).value.reversed() }
        val adapter = factory.create(String::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<String>

        try {
            adapter.fromMessage(Message.Text(rawMessage))
        } finally {
            verify(dataMessageAdapter, never()).fromMessage(any())
        }
    }
    // endregion fromMessage()

    // region toMessage()
    @Test
    fun verifyToMessageSimple() {
        whenever(dataMessageAdapter.toMessage(any()))
            .thenAnswer { Message.Text((it.arguments.first() as String).reversed()) }
        val adapter =
            factory.create(CharSequence::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<CharSequence>

        val json = (adapter.toMessage("payload") as Message.Text).value
        assertThatJson(json).node("command").isEqualTo("message")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("""{channel:"valid"}""")
        assertThatJson(json).node("data").isEqualTo("daolyap")
        verify(dataMessageAdapter).toMessage(eq("payload"))
    }

    @Test(expected = JsonEncodingException::class)
    fun verifyToMessageSimpleAsJsonFails() {
        whenever(dataMessageAdapter.toMessage(any())).thenAnswer { Message.Text("encoded") }
        val adapter = factory.create(
            CharSequence::class.java,
            arrayOf(actionCableAnnotation, dataAsJson)
        ) as MessageAdapter<CharSequence>

        try {
            adapter.toMessage("payload")
        } finally {
            verify(dataMessageAdapter).toMessage(eq("payload"))
        }
    }

    @Test
    fun verifyToMessageComplex() {
        whenever(dataMessageAdapter.toMessage(any()))
            .thenAnswer { Message.Text("""{"msg":"${it.arguments.first() as String}"}""") }
        val adapter =
            factory.create(CharSequence::class.java, arrayOf(actionCableAnnotation)) as MessageAdapter<CharSequence>

        val json = (adapter.toMessage("payload") as Message.Text).value
        assertThatJson(json).node("command").isEqualTo("message")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("""{channel:"valid"}""")
        assertThatJson(json).node("data").isString()
        assertThatJson(JSONObject(json).getString("data")).node("msg").isEqualTo("payload")
        verify(dataMessageAdapter).toMessage(eq("payload"))
    }

    @Test
    fun verifyToMessageComplexAsJson() {
        whenever(dataMessageAdapter.toMessage(any()))
            .thenAnswer { Message.Text("""{"msg":"${it.arguments.first() as String}"}""") }
        val adapter = factory.create(
            CharSequence::class.java,
            arrayOf(actionCableAnnotation, dataAsJson)
        ) as MessageAdapter<CharSequence>

        val json = (adapter.toMessage("payload") as Message.Text).value
        assertThatJson(json).node("command").isEqualTo("message")
        assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("""{channel:"valid"}""")
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.msg").isEqualTo("payload")
        verify(dataMessageAdapter).toMessage(eq("payload"))
    }
    // endregion toMessage()
}
