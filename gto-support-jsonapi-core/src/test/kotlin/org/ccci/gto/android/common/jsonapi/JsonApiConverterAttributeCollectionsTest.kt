package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelBase

class JsonApiConverterAttributeCollectionsTest {
    private val converter = JsonApiConverter.Builder()
        .addClasses(ModelCollectionAttribute::class.java)
        .build()

    @Test
    fun verifyToJsonNull() {
        val obj = ModelCollectionAttribute(99)

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelCollectionAttribute.TYPE)
        assertThatJson(json).node("data.attributes.stringSet").isAbsent()
        assertThatJson(json).node("data.attributes.integerList").isAbsent()
    }

    @Test
    fun verifyToJsonEmpty() {
        val obj = ModelCollectionAttribute(
            id = 99,
            stringSet = emptySet(),
            integerList = emptyList(),
        )

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelCollectionAttribute.TYPE)
        assertThatJson(json).node("data.attributes.stringSet").isArray.hasSize(0)
        assertThatJson(json).node("data.attributes.integerList").isArray.hasSize(0)
    }

    @Test
    fun verifyToJson() {
        val obj = ModelCollectionAttribute(
            id = 99,
            stringSet = setOf("a", "b", "c", "a"),
            integerList = listOf(1, 2, 3),
        )

        val json = converter.toJson(JsonApiObject.single(obj))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelCollectionAttribute.TYPE)
        assertThatJson(json).node("data.attributes.integerList").isArray.hasSize(3)
            .contains(1, 2, 3)
        assertThatJson(json).node("data.attributes.stringSet").isArray.hasSize(3)
            .contains("a", "b", "c")
    }

    @Test
    fun verifyFromJsonNull() {
        val obj = ModelCollectionAttribute(99)
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelCollectionAttribute::class.java)
        assertNotNull(out.dataSingle) {
            assertEquals(obj.id, it.id)
            assertNull(it.integerList)
            assertNull(it.stringSet)
        }
    }

    @Test
    fun verifyFromJsonEmpty() {
        val obj = ModelCollectionAttribute(
            id = 99,
            stringSet = emptySet(),
            integerList = emptyList(),
        )
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelCollectionAttribute::class.java)
        assertNotNull(out.dataSingle) {
            assertEquals(obj.id, it.id)
            assertNotNull(it.stringSet) { assertTrue(it.isEmpty()) }
            assertNotNull(it.integerList) { assertTrue(it.isEmpty()) }
        }
    }

    @Test
    fun verifyFromJson() {
        val obj = ModelCollectionAttribute(
            id = 99,
            stringSet = setOf("a", "b", "c", "a"),
            integerList = listOf(1, 2, 3),
        )
        val json = converter.toJson(JsonApiObject.single(obj))

        val out = converter.fromJson(json, ModelCollectionAttribute::class.java).dataSingle
        assertNotNull(out) {
            assertEquals(obj.id, it.id)
            assertEquals(setOf("a", "b", "c"), it.stringSet)
            assertEquals(listOf(1, 2, 3), it.integerList)
        }
    }

    @JsonApiType(ModelCollectionAttribute.TYPE)
    class ModelCollectionAttribute(
        id: Int? = null,
        var stringSet: Set<String>? = null,
        var integerList: List<Int>? = null,
    ) : ModelBase(id) {
        companion object {
            const val TYPE = "collection_attribute"
        }
    }
}
