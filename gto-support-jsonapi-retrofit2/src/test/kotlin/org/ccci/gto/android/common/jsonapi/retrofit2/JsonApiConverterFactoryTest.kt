package org.ccci.gto.android.common.jsonapi.retrofit2

import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent
import net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals
import net.javacrumbs.jsonunit.core.Option
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

private const val SIMPLE_SINGLE_RAW_JSON = "{data:{id:5,type:\"simple\"}}"

class JsonApiConverterFactoryTest {
    @get:Rule
    val server = MockWebServer()

    private var service = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(
            JsonApiConverterFactory(
                ModelSimple::class.java,
                ModelParent::class.java,
                ModelChild::class.java,
            ),
        )
        .build()
        .create(Service::class.java)

    @Test
    fun verifyWrappedObj() {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))

        val body = service.post(JsonApiObject.single(ModelSimple(42, "blah"))).execute().body()
        assertNotNull(body)
        assertTrue(body!!.isSingle)
        val obj = body.dataSingle
        assertNotNull(obj)
        assertEquals(5, obj!!.id)

        val request = server.takeRequest()
        assertEquals(JsonApiObject.MEDIA_TYPE, request.getHeader("Content-Type"))
        val json = request.body.readUtf8()
        assertThatJson(json).node("data").isObject()
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE))
        assertThat(json, jsonPartEquals("data.id", 42))
        assertThat(json, jsonPartEquals("data.attributes.attr1", "blah"))
        assertThat(json, jsonNodeAbsent("included"))
    }

    @Test
    fun verifyPlainObj() {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))

        val obj = service.post(ModelSimple(42, "blah")).execute().body()
        assertNotNull(obj)
        assertEquals(5, obj!!.id)

        val request = server.takeRequest()
        assertEquals(JsonApiObject.MEDIA_TYPE, request.getHeader("Content-Type"))
        val json = request.body.readUtf8()
        assertThatJson(json).node("data").isObject()
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE))
        assertThat(json, jsonPartEquals("data.id", 42))
        assertThat(json, jsonPartEquals("data.attributes.attr1", "blah"))
        assertThat(json, jsonNodeAbsent("included"))
    }

    @Test
    fun verifyPostIncludes() {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))
        val parent = ModelParent(1).apply {
            favorite = ModelChild(11, "Daniel")
            children = listOf(favorite!!, ModelChild(20, "Hey You"))
        }

        val obj = service.postInclude(parent).execute().body()
        assertNotNull(obj)
        assertEquals(5, obj!!.id)

        val request = server.takeRequest()
        assertEquals(JsonApiObject.MEDIA_TYPE, request.getHeader("Content-Type"))
        val json = request.body.readUtf8()
        assertThatJson(json).node("data").isObject()
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE))
        assertThat(json, jsonPartEquals("data.id", 1))
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"))
        assertThat(json, jsonNodeAbsent("data.attributes.children"))
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.type", ModelChild.TYPE))
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.id", parent.favorite!!.id))
        assertThat(json, jsonNodeAbsent("data.relationships.favorite.data.attributes"))
        assertThatJson(json).node("data.relationships.children.data").isArray.ofLength(2)
        assertThatJson(json).node("included").isArray.ofLength(1)
        assertThatJson(json).node("included").matches(
            allOf(
                hasItem(
                    jsonEquals<Any>("{type:'child',id:11,attributes:{name:'Daniel'}}")
                        .`when`(Option.IGNORING_EXTRA_FIELDS),
                ),
                not(
                    hasItem(
                        jsonEquals<Any>("{type:'child',id:20,attributes:{name:'Hey You'}}")
                            .`when`(Option.IGNORING_EXTRA_FIELDS),
                    ),
                ),
            )
        )
    }

    internal interface Service {
        @POST("/")
        fun post(@Body model: JsonApiObject<ModelSimple?>?): Call<JsonApiObject<ModelSimple>>

        @POST("/")
        fun post(@Body model: ModelSimple?): Call<ModelSimple>

        @POST("/")
        fun postInclude(
            @Body
            @JsonApiInclude("favorite")
            model: ModelParent?
        ): Call<ModelSimple>
    }

    abstract class ModelBase(
        @field:JsonApiId var id: Int = 0,
    )

    @JsonApiType(ModelSimple.TYPE)
    class ModelSimple @JvmOverloads constructor(id: Int = 0, var attr1: String? = null) : ModelBase(id) {
        companion object {
            const val TYPE = "simple"
        }
    }

    @JsonApiType(ModelParent.TYPE)
    class ModelParent(id: Int = 0) : ModelBase(id) {
        companion object {
            const val TYPE = "parent"
        }

        var children = emptyList<ModelChild>()
        var favorite: ModelChild? = null
    }

    @JsonApiType(ModelChild.TYPE)
    class ModelChild(id: Int = 0, var name: String) : ModelBase(id) {
        companion object {
            const val TYPE = "child"
        }
    }
}
