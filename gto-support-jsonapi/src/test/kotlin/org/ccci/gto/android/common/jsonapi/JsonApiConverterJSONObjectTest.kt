package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelBase
import org.json.JSONArray
import org.json.JSONObject

private const val RAW_JSON_OBJECT = "{'a': 'b'}"
private const val RAW_JSON_ARRAY = "[1, 'pizza', $RAW_JSON_OBJECT]"

class JsonApiConverterJSONObjectTest {
    @Test
    fun verifyToJsonNull() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelJSONObject::class.java)
            .build()
        val obj = ModelJSONObject(99)

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelJSONObject.TYPE)
        assertThatJson(json).node("data.attributes.jsonObject").isAbsent
        assertThatJson(json).node("data.attributes.jsonArray").isAbsent
    }

    @Test
    fun verifyToJsonEmpty() {
        val converter = JsonApiConverter.Builder().addClasses(ModelJSONObject::class.java).build()
        val obj = ModelJSONObject(
            id = 99,
            jsonObject = JSONObject(),
            jsonArray = JSONArray(),
        )

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelJSONObject.TYPE)
        assertThatJson(json).node("data.attributes.jsonObject").isObject()
        assertThatJson(json).node("data.attributes.jsonObject").isEqualTo("{}")
        assertThatJson(json).node("data.attributes.jsonArray").isArray.ofLength(0)
    }

    @Test
    fun verifyToJson() {
        val converter = JsonApiConverter.Builder().addClasses(ModelJSONObject::class.java).build()
        val obj = ModelJSONObject(
            id = 99,
            jsonObject = JSONObject(RAW_JSON_OBJECT),
            jsonArray = JSONArray(RAW_JSON_ARRAY),
        )

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelJSONObject.TYPE)
        assertThatJson(json).node("data.attributes.jsonObject").isObject()
        assertThatJson(json).node("data.attributes.jsonObject").isEqualTo(RAW_JSON_OBJECT)
        assertThatJson(json).node("data.attributes.jsonArray").isArray
        assertThatJson(json).node("data.attributes.jsonArray").isEqualTo(RAW_JSON_ARRAY)
    }

    @Test
    fun verifyFromJsonNull() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelJSONObject::class.java)
            .build()
        val obj = ModelJSONObject(99)
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelJSONObject::class.java)
        assertNotNull(out.dataSingle) {
            assertEquals(obj.id, it.id)
            assertNull(it.jsonObject)
            assertNull(it.jsonArray)
        }
    }

    @Test
    fun verifyFromJsonEmpty() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelJSONObject::class.java)
            .build()
        val obj = ModelJSONObject(
            id = 99,
            jsonObject = JSONObject(),
            jsonArray = JSONArray(),
        )
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelJSONObject::class.java)
        assertNotNull(out.dataSingle) {
            assertEquals(obj.id, it.id)
            assertNotNull(it.jsonObject) { assertTrue(it.isEmpty) }
            assertNotNull(it.jsonArray) { assertTrue(it.isEmpty) }
        }
    }

    @Test
    fun verifyFromJson() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelJSONObject::class.java)
            .build()
        val obj = ModelJSONObject(
            id = 99,
            jsonObject = JSONObject(RAW_JSON_OBJECT),
            jsonArray = JSONArray(RAW_JSON_ARRAY),
        )
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelJSONObject::class.java).dataSingle
        assertNotNull(out) {
            assertEquals(obj.id, it.id)
            assertEquals(obj.jsonObject?.toString(), it.jsonObject?.toString())
            assertEquals(obj.jsonArray?.toString(), it.jsonArray?.toString())
        }
    }

    @JsonApiType(ModelJSONObject.TYPE)
    class ModelJSONObject(
        id: Int? = null,
        var jsonObject: JSONObject? = null,
        var jsonArray: JSONArray? = null,
    ) : ModelBase(id) {
        companion object {
            const val TYPE = "json_object"
        }
    }
}
