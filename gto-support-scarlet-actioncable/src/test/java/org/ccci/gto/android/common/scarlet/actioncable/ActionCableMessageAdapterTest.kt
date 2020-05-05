package org.ccci.gto.android.common.scarlet.actioncable

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert
import org.ccci.gto.android.common.scarlet.actioncable.model.Subscribe
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class ActionCableMessageAdapterTest {
    private val factory = ActionCableMessageAdapterFactory()

    // region Subscribe MessageAdapter
    @Test
    fun verifySubscribeMessageAdapterToMessage() {
        val adapter = factory.create(Subscribe::class.java, emptyArray()) as MessageAdapter<Subscribe>
        val message = adapter.toMessage(Subscribe("channel"))
        assertThat(message, instanceOf(Message.Text::class.java))

        val json = (message as Message.Text).value
        JsonFluentAssert.assertThatJson(json).node("command").isEqualTo("subscribe")
        JsonFluentAssert.assertThatJson(JSONObject(json).getString("identifier")).isEqualTo("{channel:\"channel\"}")
    }

    @Test
    fun verifySubscribeMessageAdapterFromMessage() {
        val adapter = factory.create(Subscribe::class.java, emptyArray()) as MessageAdapter<Subscribe>
        val subscribe =
            adapter.fromMessage(Message.Text("""{"identifier":"{\"channel\":\"channel\"}","command":"subscribe"}"""))
        assertEquals("subscribe", subscribe.command)
        assertEquals("channel", subscribe.identifier.channel)
    }
    // endregion Subscribe MessageAdapter

    // region Unsupported Type
    @Test(expected = IllegalArgumentException::class)
    fun verifyUnsupportedTypeThrowsException() {
        factory.create(String::class.java, emptyArray())
    }
    // endregion Unsupported Type
}
