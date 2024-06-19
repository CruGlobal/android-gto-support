package org.ccci.gto.android.common.jsonapi.retrofit2

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import net.javacrumbs.jsonunit.assertj.assertThatJson
import net.javacrumbs.jsonunit.core.Option
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiFields
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude
import org.ccci.gto.android.common.jsonapi.util.Includes
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val SIMPLE_SINGLE_RAW_JSON = "{data:{id:5,type:\"simple\"}}"

class JsonApiConverterFactoryTest {
    @get:Rule
    val server = MockWebServer()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .validateEagerly(true)
        .addConverterFactory(
            JsonApiConverterFactory(
                ModelSimple::class.java,
                ModelParent::class.java,
                ModelChild::class.java,
            ),
        )
        .build()
    private val service: Service = retrofit.create()

    @Test
    fun verifyWrappedObj() {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))

        val response = service.post(JsonApiObject.single(ModelSimple(42, "blah"))).execute()
        assertNotNull(response.body()) { body ->
            assertTrue(body.isSingle)
            assertNotNull(body.dataSingle) { obj ->
                assertEquals(5, obj.id)
            }
        }

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

        val response = service.post(ModelSimple(42, "blah")).execute()
        assertNotNull(response.body()) {
            assertEquals(5, it.id)
        }

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
    fun `RequestBody - Includes`() = runTest {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))
        val parent = ModelParent(1).apply {
            favorite = ModelChild(11, "Daniel")
            children = listOf(favorite!!, ModelChild(20, "Hey You"))
        }

        val obj = service.postInclude(parent)
        assertNotNull(obj) { assertEquals(5, it.id) }

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

            withOptions(Option.IGNORING_EXTRA_FIELDS)
                .node("included[0]").isEqualTo("{type:'child',id:11,attributes:{name:'Daniel'}}")
        }
    }

    @Test
    fun `RequestBody - Fields - Single`() = runTest {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))

        val obj = service.postFieldsSingle(ModelSimple(1, "attr1", "attr2"))
        assertNotNull(obj) { assertEquals(5, it.id) }

        val request = server.takeRequest()
        assertEquals(JsonApiObject.MEDIA_TYPE, request.getHeader("Content-Type"))
        assertThatJson(request.body.readUtf8()) {
            node("data").isObject
            node("data.type").isEqualTo(ModelSimple.TYPE)
            node("data.id").isEqualTo(1)
            node("data.attributes").isEqualTo("""{attr2: "attr2"}""")
            node("data.attributes.attr1").isAbsent()
        }
    }

    @Test
    fun `RequestBody - Fields - Multiple`() = runTest {
        server.enqueue(MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON))
        val parent = ModelParent(1).apply {
            favorite = ModelChild(11, "Daniel")
            children = listOf(favorite!!, ModelChild(20, "Hey You"))
        }

        val obj = service.postFieldsMultiple(parent)
        assertNotNull(obj) { assertEquals(5, it.id) }

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
            node("data.relationships.children").isAbsent()

            node("included").isArray.hasSize(1)
            node("included[0]").isEqualTo("{type:'child',id:11,attributes:{height:0}}")
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

    // region Retrofit Validation
    @Test
    fun `Retrofit Validation - Error - RequestBody - Wrapped Object`() {
        assertFailsWith<IllegalArgumentException> {
            retrofit.create<ServiceValidationErrorRequestBodyWrappedObject>()
        }
    }

    @Test
    fun `Retrofit Validation - Error - RequestBody - Collection`() {
        assertFailsWith<IllegalArgumentException> {
            retrofit.create<ServiceValidationErrorRequestBodyCollection>()
        }
    }

    @Test
    fun `Retrofit Validation - Error - RequestBody - Plain Object`() {
        assertFailsWith<IllegalArgumentException> {
            retrofit.create<ServiceValidationErrorRequestBodyPlainObject>()
        }
    }

    @Test
    fun `Retrofit Validation - Error - Response - Wrapped Object`() {
        assertFailsWith<IllegalArgumentException> {
            retrofit.create<ServiceValidationErrorResponseWrappedObject>()
        }
    }

    @Test
    fun `Retrofit Validation - Error - Response - Plain Object`() {
        assertFailsWith<IllegalArgumentException> {
            retrofit.create<ServiceValidationErrorResponsePlainObject>()
        }
    }

    interface ServiceValidationErrorRequestBodyWrappedObject {
        @POST("/")
        suspend fun post(@Body data: JsonApiObject<ModelInvalid>): ResponseBody
    }
    interface ServiceValidationErrorRequestBodyCollection {
        @POST("/")
        suspend fun post(@Body data: List<ModelInvalid>): ResponseBody
    }
    interface ServiceValidationErrorRequestBodyPlainObject {
        @POST("/")
        suspend fun post(@Body data: ModelInvalid): ResponseBody
    }
    interface ServiceValidationErrorResponseWrappedObject {
        @GET("/")
        suspend fun get(): JsonApiObject<ModelInvalid>
    }
    interface ServiceValidationErrorResponsePlainObject {
        @GET("/")
        suspend fun get(): ModelInvalid
    }
    // endregion Retrofit Validation

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
        suspend fun postInclude(
            @Body
            @JsonApiInclude("favorite")
            model: ModelParent?,
        ): ModelSimple?

        @POST("/")
        suspend fun postFieldsSingle(
            @Body
            @JsonApiFields(type = ModelSimple.TYPE, "attr2")
            model: ModelSimple?,
        ): ModelSimple?

        @POST("/")
        suspend fun postFieldsMultiple(
            @Body
            @JsonApiFields(type = ModelParent.TYPE, "favorite")
            @JsonApiFields(type = ModelChild.TYPE, "height")
            @JsonApiInclude("favorite")
            model: ModelParent?,
        ): ModelSimple?
    }

    abstract class ModelBase(
        @field:JsonApiId var id: Int = 0,
    )

    @JsonApiType(ModelSimple.TYPE)
    class ModelSimple @JvmOverloads constructor(
        id: Int = 0,
        var attr1: String? = null,
        var attr2: String? = null,
    ) : ModelBase(id) {
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
    class ModelChild(id: Int = 0, var name: String, var height: Int = 0) : ModelBase(id) {
        companion object {
            const val TYPE = "child"
        }
    }

    @JsonApiType(ModelInvalid.TYPE)
    class ModelInvalid : ModelBase(0) {
        companion object {
            const val TYPE = "invalid"
        }
    }
}
