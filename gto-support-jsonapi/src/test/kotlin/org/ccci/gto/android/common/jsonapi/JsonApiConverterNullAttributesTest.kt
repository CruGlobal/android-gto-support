package org.ccci.gto.android.common.jsonapi

import net.javacrumbs.jsonunit.JsonMatchers
import net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER
import net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_ARRAY_ITEMS
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelBase
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class JsonApiConverterNullAttributesTest {
    private val converter = JsonApiConverter.Builder()
        .addClasses(ModelParent::class.java, ModelChild::class.java)
        .build()

    @Test
    fun verifyNullAttributeSerialization() {
        val parent = createObj()
        val json = converter.toJson(
            JsonApiObject.single(parent),
            JsonApiConverter.Options.builder()
                .serializeNullAttributes(ModelChild.TYPE)
                .build(),
        )
        assertThatJson(json).node("data").isObject()
        assertThat(json, JsonMatchers.jsonPartEquals("data.type", ModelParent.TYPE))
        assertThatJson(json).node("data.attributes").isAbsent
        assertThatJson(json).node("included").isArray.ofLength(2)
        assertThatJson(json).node("included")
            .`when`(IGNORING_EXTRA_ARRAY_ITEMS, IGNORING_ARRAY_ORDER)
            .isEqualTo("[{type:'child',id:11,attributes:{name:'Daniel'}}]")
        assertThatJson(json).node("included")
            .`when`(IGNORING_EXTRA_ARRAY_ITEMS, IGNORING_ARRAY_ORDER)
            .isEqualTo("[{type:'child',id:20,attributes:{name:null}}]")
    }

    private fun createObj(): ModelParent {
        val parent = ModelParent(null)
        parent.children.add(ModelChild(11, "Daniel"))
        parent.children.add(ModelChild(20, null))
        return parent
    }

    @JsonApiType(ModelParent.TYPE)
    class ModelParent(
        var name: String? = null,
        val children: MutableList<ModelChild> = mutableListOf(),
    ) : ModelBase() {
        companion object {
            const val TYPE = "parent"
        }
    }

    @JsonApiType(ModelChild.TYPE)
    class ModelChild(id: Int? = null, var name: String? = null) : ModelBase(id) {
        companion object {
            const val TYPE = "child"
        }
    }
}
