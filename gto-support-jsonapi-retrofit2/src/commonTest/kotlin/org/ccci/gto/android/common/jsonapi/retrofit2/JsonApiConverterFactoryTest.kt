package org.ccci.gto.android.common.jsonapi.retrofit2

import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import net.javacrumbs.jsonunit.assertj.assertThatJson
import net.javacrumbs.jsonunit.core.Option
import net.javacrumbs.jsonunit.core.internal.Options
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude
import org.ccci.gto.android.common.jsonapi.util.Includes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
        assertThatJson(request.body.readUtf8()) {
            node("data").isObject
            node("data.type").isEqualTo(ModelSimple.TYPE)
            node("data.id").isEqualTo(42)
            node("data.attributes.attr1").isEqualTo("blah")
            node("included").isAbsent()
        }
    }

    @Test
    fun verifyPlainObj() {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))

        val obj = service.post(ModelSimple(42, "blah")).execute().body()
        assertNotNull(obj)
        assertEquals(5, obj!!.id)

        val request = server.takeRequest()
        assertEquals(JsonApiObject.MEDIA_TYPE, request.getHeader("Content-Type"))
        assertThatJson(request.body.readUtf8()) {
            node("data").isObject
            node("data.type").isEqualTo(ModelSimple.TYPE)
            node("data.id").isEqualTo(42)
            node("data.attributes.attr1").isEqualTo("blah")
            node("included").isAbsent()
        }
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
        assertThatJson(request.body.readUtf8()) {
            node("data").isObject
            node("data.type").isEqualTo(ModelParent.TYPE)
            node("data.id").isEqualTo(1)
            node("data.attributes.favorite").isAbsent()
            node("data.attributes.children").isAbsent()
            node("data.relationships.favorite.data.type").isEqualTo(ModelChild.TYPE)
            node("data.relationships.favorite.data.id").isEqualTo(parent.favorite!!.id)
            node("data.relationships.favorite.data.attributes").isAbsent()
            node("data.relationships.children.data").isArray.hasSize(2)
            node("included").isArray.hasSize(1)

            withOptions(Options(Option.IGNORING_EXTRA_FIELDS))
                .node("included[0]").isEqualTo("{type:'child',id:11,attributes:{name:'Daniel'}}")
        }
    }

    @Test
    fun `RequestBody - Collection`() = runTest {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))
        val body = listOf(
            ModelSimple(1, "first"),
            ModelSimple(2, "second"),
        )

        val obj = service.collectionRequestBody(body)
        assertNotNull(obj) { assertEquals(5, it.id) }

        val request = server.takeRequest()
        val json = request.body.readUtf8()
        assertThatJson(json) {
            node("data").isArray.hasSize(2)
            node("data[0].id").isEqualTo(1)
            node("data[0].type").isEqualTo(ModelSimple.TYPE)
            node("data[0].attributes.attr1").isEqualTo("first")
            node("data[1].id").isEqualTo(2)
            node("data[1].type").isEqualTo(ModelSimple.TYPE)
            node("data[1].attributes.attr1").isEqualTo("second")
        }
    }

    @Test
    fun `Includes query parameter encoding`() = runTest {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))
        val includes = Includes("a", "b.c")

        val obj = service.includesQueryParam(includes)
        assertNotNull(obj) { assertEquals(5, it.id) }

        val request = server.takeRequest()
        assertEquals("a,b.c", request.requestUrl?.queryParameter(JsonApiParams.PARAM_INCLUDE))
    }

    internal interface Service {
        @GET("/")
        suspend fun includesQueryParam(@Query(JsonApiParams.PARAM_INCLUDE) includes: Includes): ModelSimple

        @POST("/")
        fun post(@Body model: JsonApiObject<ModelSimple>?): Call<JsonApiObject<ModelSimple>>

        @POST("/")
        suspend fun collectionRequestBody(@Body model: List<ModelSimple>): ModelSimple?

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
