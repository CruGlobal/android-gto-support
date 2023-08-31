package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelBase
import org.hamcrest.Matchers.containsInAnyOrder

class JsonApiConverterSparseFieldsetTest {
    private val favorite = ModelChild(
        id = 11,
        name = "Daniel",
        age = 34,
    )
    private val parent = ModelParent(
        name = "Padre",
        age = 65,
        children = listOf(
            favorite,
            ModelChild(
                id = 20,
                name = "Hey You",
                age = 15,
            ),
        ),
        favorite = favorite,
    )

    @Test
    fun verifyToJsonSparseFieldsetSimple() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelParent::class.java, ModelChild::class.java)
            .build()
        val options = JsonApiConverter.Options.builder()
            .fields(ModelParent.TYPE, "name")
            .build()

        val json = converter.toJson(JsonApiObject.single(parent), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes").matches(jsonEquals<Any>("{name:'Padre'}"))
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.relationships.favorite").isAbsent
        assertThatJson(json).node("data.attributes.children").isAbsent
        assertThatJson(json).node("data.relationships.children").isAbsent
        assertThatJson(json).node("included").isAbsent
    }

    @Test
    fun verifyToJsonSparseFieldsetRelated() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelParent::class.java, ModelChild::class.java)
            .build()
        val options = JsonApiConverter.Options.builder()
            .fields(ModelChild.TYPE, "name")
            .build()

        val json = converter.toJson(JsonApiObject.single(parent), options,)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("included").isArray.ofLength(2)
        assertThatJson(json).node("included").matches(
            containsInAnyOrder<Any>(
                jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}"),
                jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}"),
            ),
        )
    }

    @Test
    fun verifyToJsonSparseFieldsetRelations() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelParent::class.java, ModelChild::class.java)
            .build()
        val options = JsonApiConverter.Options.builder()
            .fields(ModelParent.TYPE, "name", "favorite")
            .fields(ModelChild.TYPE, "name")
            .build()

        val json = converter.toJson(JsonApiObject.single(parent), options,)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.type").isEqualTo(ModelParent.TYPE)
        assertThatJson(json).node("data.attributes").matches(jsonEquals<Any>("{name:'Padre'}"))
        assertThatJson(json).node("data.attributes.favorite").isAbsent
        assertThatJson(json).node("data.relationships.favorite").isPresent
        assertThatJson(json).node("data.attributes.children").isAbsent
        assertThatJson(json).node("data.relationships.children").isAbsent
        assertThatJson(json).node("included").isArray.ofLength(1)
        assertThatJson(json).node("included")
            .matches(containsInAnyOrder(jsonEquals<Any>("{type:'child',id:11,attributes:{name:'Daniel'}}")))
    }

    @JsonApiType(ModelParent.TYPE)
    class ModelParent(
        var name: String? = null,
        var age: Long = 0,
        var children: List<ModelChild> = emptyList(),
        var favorite: ModelChild? = null, // everyone has a favorite child
        id: Int? = null,
    ) : ModelBase(id) {
        companion object {
            const val TYPE = "parent"
        }
    }

    @JsonApiType(ModelChild.TYPE)
    class ModelChild(
        var name: String? = null,
        var age: Long = 0,
        id: Int? = null,
    ) : ModelBase(id) {
        companion object {
            const val TYPE = "child"
        }
    }
}
