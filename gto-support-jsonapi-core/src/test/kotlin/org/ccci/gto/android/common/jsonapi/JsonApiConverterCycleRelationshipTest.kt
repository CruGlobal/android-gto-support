package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.model.ModelBase

class JsonApiConverterCycleRelationshipTest {
    private val obj = ModelCycle(1).apply {
        cycle = ModelCycle(2, this)
    }

    @Test
    fun `toJson(cycleObj) - Include relationships - none`() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelCycle::class.java)
            .build()
        val options = Options.builder().include().build()

        val json = converter.toJson(JsonApiObject.single(obj), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelCycle.JSONAPI_TYPE)
        assertThatJson(json).node("data.relationships.cycle.data.id").isEqualTo(obj.cycle?.id)
        assertThatJson(json).node("included").isAbsent
    }

    @Test
    fun `toJson(cycleObj) - Include relationships - depth 1`() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelCycle::class.java)
            .build()
        val options = Options.builder().include("cycle").build()

        val json = converter.toJson(JsonApiObject.single(obj), options)
        assertThatJson(json).node("data").isObject()
        assertThatJson(json).node("data.id").isEqualTo(obj.id)
        assertThatJson(json).node("data.type").isEqualTo(ModelCycle.JSONAPI_TYPE)
        assertThatJson(json).node("data.relationships.cycle.data.id").isEqualTo(obj.cycle?.id)
        assertThatJson(json).node("included").isArray.ofLength(1)
        assertThatJson(json).node("included[0].id").isEqualTo(obj.cycle?.id)
        assertThatJson(json).node("included[0].type").isEqualTo(ModelCycle.JSONAPI_TYPE)
        assertThatJson(json).node("included[0].relationships.cycle.data.id").isEqualTo(obj.cycle?.cycle?.id)
    }

    @JsonApiType(ModelCycle.JSONAPI_TYPE)
    class ModelCycle(
        id: Int,
        var cycle: ModelCycle? = null,
    ) : ModelBase(id) {
        companion object {
            const val JSONAPI_TYPE = "cycle"
        }
    }
}
