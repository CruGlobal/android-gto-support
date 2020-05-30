package org.ccci.gto.android.common.jsonapi.scarlet

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import org.ccci.gto.android.common.jsonapi.JsonApiConverter
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class JsonApiMessageAdapterTest {
    private val jsonApiConverter = JsonApiConverter.Builder().addClasses(Obj::class.java).build()
    private lateinit var factory: MessageAdapter.Factory

    @Before
    fun setup() {
        factory = JsonApiMessageAdapterFactory(jsonApiConverter)
    }

    // region ObjectMessageAdapter tests
    @Test
    fun verifyObjectMessageAdapterFromMessage() {
        val rawMessage = """{"data":{"type":"obj", "attributes":{"attr": "value"}}}"""
        val adapter = factory.create(Obj::class.java, emptyArray()) as MessageAdapter<Obj>

        val obj = adapter.fromMessage(Message.Text(rawMessage))
        assertEquals("value", obj.attr)
    }

    @Test(expected = Exception::class)
    fun verifyObjectMessageAdapterFromMessageNullData() {
        val rawMessage = """{"data":null}"""
        val adapter = factory.create(Obj::class.java, emptyArray()) as MessageAdapter<Obj>

        adapter.fromMessage(Message.Text(rawMessage))
        fail()
    }

    @Test(expected = Exception::class)
    fun verifyObjectMessageAdapterFromMessageInvalidType() {
        val rawMessage = """{"data":{"type":"invalid", "attributes":{"attr": "value"}}}"""
        val adapter = factory.create(Obj::class.java, emptyArray()) as MessageAdapter<Obj>

        adapter.fromMessage(Message.Text(rawMessage))
        fail()
    }

    @Test(expected = Exception::class)
    fun verifyObjectMessageAdapterFromMessageMultipleObjects() {
        val rawMessage = """{"data":[{"type":"obj"}, {"type":"obj"}]}"""
        val adapter = factory.create(Obj::class.java, emptyArray()) as MessageAdapter<Obj>

        adapter.fromMessage(Message.Text(rawMessage))
        fail()
    }
    // endregion ObjectMessageAdapter tests

    @JsonApiType("obj")
    class Obj {
        var attr: String? = null
    }
}
