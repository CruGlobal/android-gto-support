package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import net.javacrumbs.jsonunit.core.Option
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelChild
import org.ccci.gto.android.common.jsonapi.model.ModelParent
import org.hamcrest.Matchers.containsInAnyOrder

class JsonApiConverterRelatedTest {
    private val converter = JsonApiConverter.Builder()
        .addClasses(ModelParent::class.java, ModelChild::class.java)
        .build()

    private val favorite = ModelChild(id = 11, name = "Daniel")
    private val child2 = ModelChild(id = 20, name = "Hey You")
    private val child3 = ModelChild(name = "Child with no name")
    private val parent = ModelParent(
        id = 1,
        favorite = favorite,
        children = listOf(favorite, child2),
        orphans = arrayOf(favorite, child2)
    )

    @Test
    fun verifyToJsonRelationships() {
        parent.children = listOf(favorite, child2, child3)
        parent.orphans = arrayOf(favorite, child2, child3)

        val json = converter.toJson(JsonApiObject.single(parent))
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.attributes.children").isAbsent
        assertThatJson(json).node("data.relationships.favorite.data.type").isEqualTo(ModelChild.TYPE)
        assertThatJson(json).node("data.relationships.favorite.data.id").isEqualTo(favorite.id)
        assertThatJson(json).node("data.relationships.favorite.data.attributes").isAbsent
        assertThatJson(json).node("data.relationships.children.data").isArray.ofLength(2)
        assertThatJson(json).node("data.relationships.orphans.data").isArray.ofLength(2)
        assertThatJson(json).node("included").isArray.ofLength(2)
        assertThatJson(json).node("included").matches(
            containsInAnyOrder(
                jsonEquals<Any>("{type:'child',id:11,attributes:{name:'Daniel'}}")
                    .`when`(Option.IGNORING_EXTRA_FIELDS),
                jsonEquals<Any>("{type:'child',id:20,attributes:{name:'Hey You'}}")
                    .`when`(Option.IGNORING_EXTRA_FIELDS),
            ),
        )
    }

    @Test
    fun verifyFromJsonRelationships() {
        val json = converter.toJson(JsonApiObject.single(parent))

        val obj = converter.fromJson(json, ModelParent::class.java)
        assertTrue(obj.isSingle)
        assertNotNull(obj.dataSingle) {
            assertEquals(parent.id, it.id)
            assertEquals(parent.favorite, it.favorite)
            assertContentEquals(parent.children, it.children)
            assertContentEquals(parent.orphans, it.orphans)
        }
    }

    @Test
    fun verifyFromJsonRelationshipsMissingJson() {
        val json = """{"data":{"type":"parent","id":1}}"""

        val obj = converter.fromJson(json, ModelParent::class.java)
        assertTrue(obj.isSingle)
        assertNotNull(obj.dataSingle) {
            assertEquals(1, it.id)
            assertNull(it.favorite)
        }
    }

    @Test
    fun verifyToJsonIncludeNothing() {
        val options = JsonApiConverter.Options
            .include()

        val json = converter.toJson(JsonApiObject.single(parent), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.attributes.children").isAbsent
        assertThatJson(json).node("data.relationships.favorite.data.id").isEqualTo(favorite.id)
        assertThatJson(json).node("data.relationships.favorite.data.type").isEqualTo(ModelChild.TYPE)
        assertThatJson(json).node("data.relationships.favorite.data.attributes").isAbsent
        assertThatJson(json).node("data.relationships.children.data").isArray.ofLength(2)
        assertThatJson(json).node("included").isAbsent
    }

    @Test
    fun verifyToJsonIncludePartial() {
        val options = JsonApiConverter.Options
            .include("favorite")

        val json = converter.toJson(JsonApiObject.single(parent), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.attributes.children").isAbsent
        assertThatJson(json).node("data.relationships.favorite.data.id").isEqualTo(favorite.id)
        assertThatJson(json).node("data.relationships.favorite.data.type").isEqualTo(ModelChild.TYPE)
        assertThatJson(json).node("data.relationships.favorite.data.attributes").isAbsent
        assertThatJson(json).node("data.relationships.children.data").isArray.ofLength(2)
        assertThatJson(json).node("included").isArray.ofLength(1)
        assertThatJson(json).node("included").matches(
            containsInAnyOrder(
                jsonEquals<Any>("{type:'child',id:11,attributes:{name:'Daniel'}}")
                    .`when`(Option.IGNORING_EXTRA_FIELDS,),
            ),
        )
    }

    @Test
    fun verifyFromJsonIncludePartial() {
        val options = JsonApiConverter.Options
            .include("favorite")
        val json = converter.toJson(JsonApiObject.single(parent), options)

        val obj = converter.fromJson(json, ModelParent::class.java)
        assertTrue(obj.isSingle)
        assertNotNull(obj.dataSingle) {
            assertEquals(parent.id, it.id)
            assertFalse(it.isPlaceholder)
            assertNotNull(it.favorite) {
                assertFalse(it.isPlaceholder)
                assertEquals(parent.favorite, it)
            }

            assertEquals(2, it.children.size)
            assertFalse(it.children[0].isPlaceholder)
            assertTrue(it.children[0].postCreateCalled)
            assertSame(it.favorite, it.children[0])
            assertEquals(parent.children[0], it.children[0])

            assertTrue(it.children[1].isPlaceholder)
            assertFalse(it.children[1].postCreateCalled)
        }
    }

    @Test
    fun verifyToJsonIncludeNoId() {
        val options = JsonApiConverter.Options.builder()
            .include("favorite")
            .includeObjectsWithNoId(true)
            .build()
        favorite.id = null
        parent.children = listOf(favorite)
        parent.orphans = arrayOf(favorite)

        val json = converter.toJson(JsonApiObject.single(parent), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.attributes.children").isAbsent

        assertThatJson(json).node("data.relationships.favorite.data").isAbsent
        assertThatJson(json).node("data.relationships.children.data").isArray.ofLength(0)
        assertThatJson(json).node("included").isArray.ofLength(1)
        assertThatJson(json).node("included")
            .matches(containsInAnyOrder(jsonEquals<Any>("{type:'child',attributes:{name:'Daniel'}}")))
    }
}
