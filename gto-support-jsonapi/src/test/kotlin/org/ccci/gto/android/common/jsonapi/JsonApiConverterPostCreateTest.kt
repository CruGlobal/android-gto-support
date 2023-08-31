package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject.Companion.single
import org.ccci.gto.android.common.jsonapi.model.ModelBase
import org.ccci.gto.android.common.jsonapi.model.ModelSimple

class JsonApiConverterPostCreateTest {
    @Test(expected = IllegalArgumentException::class)
    fun verifyConverterPostCreateParams() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateParameters::class.java)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyConverterPostCreateCheckedException() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateCheckedException::class.java)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyConverterPostCreateNotFinalMethod() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateNotFinalClass::class.java)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyConverterPostCreateStaticMethod() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateStaticMethod::class.java)
            .build()
    }

    @Test
    fun verifyConverterPostCreateFinalClass() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateFinalClass::class.java)
            .build()
    }

    @Test
    fun verifyConverterPostCreateFinalMethod() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreateFinalMethod::class.java)
            .build()
    }

    @Test
    fun verifyConverterPostCreatePrivateMethod() {
        JsonApiConverter.Builder()
            .addClasses(ModelPostCreatePrivateMethod::class.java)
            .build()
    }

    @Test
    fun verifyConverterPostCreateSimple() {
        val converter = JsonApiConverter.Builder()
            .addClasses(ModelSimple::class.java)
            .build()
        val source = ModelSimple(99)

        val output = converter.fromJson(converter.toJson(single(source)), ModelSimple::class.java)
        assertNotNull(output.dataSingle) {
            assertEquals(99, it.id)
            assertTrue(it.postCreateCalled)
        }
    }

    @Test
    fun verifyConverterPostCreateMultiple() {
        val converter = JsonApiConverter.Builder().addClasses(ModelDuplicatePostCreate::class.java).build()
        val source = ModelDuplicatePostCreate()

        val output = converter.fromJson(converter.toJson(single(source)), ModelDuplicatePostCreate::class.java)
        assertNotNull(output.dataSingle) {
            assertTrue(it.postCreateCalled)
            assertTrue(it.privatePostCreateCalled)
        }
    }

    @JsonApiType("duplicate")
    class ModelDuplicatePostCreate : ModelBase() {
        @JsonApiIgnore
        var privatePostCreateCalled = false

        @JsonApiPostCreate
        private fun privatePostCreate() {
            check(!privatePostCreateCalled) { "privatePostCreate() should only be called once" }
            privatePostCreateCalled = true
        }
    }

    @JsonApiType("params")
    private class ModelPostCreateParameters {
        @JsonApiPostCreate
        private fun postCreate(param1: String) = Unit
    }

    @JsonApiType("exception")
    private class ModelPostCreateCheckedException {
        @JsonApiPostCreate
        @Throws(Exception::class)
        private fun postCreate() = Unit
    }

    @JsonApiType("staticmethod")
    private class ModelPostCreateStaticMethod {
        companion object {
            @JvmStatic
            @JsonApiPostCreate
            fun postCreate() = Unit
        }
    }

    @JsonApiType("notfinalclass")
    private open class ModelPostCreateNotFinalClass {
        @JsonApiPostCreate
        open fun postCreate() = Unit
    }

    @JsonApiType("finalclass")
    private class ModelPostCreateFinalClass {
        @JsonApiPostCreate
        private fun postCreate() = Unit
    }

    @JsonApiType("finalmethod")
    private open class ModelPostCreateFinalMethod {
        @JsonApiPostCreate
        fun postCreate() = Unit
    }

    @JsonApiType("finalmethod")
    private class ModelPostCreatePrivateMethod {
        @JsonApiPostCreate
        private fun postCreate() = Unit
    }
}
