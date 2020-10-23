package org.ccci.gto.android.common.okta.oidc.net

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.net.ConnectionParameters
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasEntry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val HEADER1 = "Header-One"
private const val HEADER2 = "Header-Two"
private const val PARAM1 = "param1"
private const val PARAM2 = "param2"
private const val VALUE1 = "value1"
private const val VALUE2 = "value2"
private const val VALUE3 = "value3"
private const val VALUE4 = "value4"

private val HEADERS = mapOf(HEADER1 to VALUE1, HEADER2 to VALUE2)

@RunWith(AndroidJUnit4::class)
class OkHttpOktaHttpClientTest {
    @get:Rule
    val server = MockWebServer()

    private val client = OkHttpOktaHttpClient()

    @Test
    fun verifyGetRequest() {
        server.enqueue(MockResponse())
        val url = server.url("/")

        val params = ConnectionParameters.ParameterBuilder()
            .setRequestMethod(ConnectionParameters.RequestMethod.GET)
            .setRequestProperties(HEADERS)
            .create()
        client.connect(Uri.parse(url.toString()), params)
        assertEquals(1, server.requestCount)
        val request = server.takeRequest()
        assertEquals(url, request.requestUrl)
        assertEquals("GET", request.method)
        assertEquals(VALUE1, request.headers.get(HEADER1))
        assertEquals(VALUE2, request.headers.get(HEADER2))
    }

    @Test
    fun verifyPostRequest() {
        server.enqueue(MockResponse())
        val url = server.url("/")

        val params = ConnectionParameters.ParameterBuilder()
            .setRequestMethod(ConnectionParameters.RequestMethod.POST)
            .setRequestProperties(HEADERS)
            .setPostParameters(mapOf(PARAM1 to VALUE3, PARAM2 to VALUE4))
            .create()
        client.connect(Uri.parse(url.toString()), params)
        assertEquals(1, server.requestCount)
        val request = server.takeRequest()
        assertEquals(url, request.requestUrl)
        assertEquals("POST", request.method)
        assertEquals(VALUE1, request.headers.get(HEADER1))
        assertEquals(VALUE2, request.headers.get(HEADER2))

        val postParams = HttpUrl.Builder()
            .scheme("http").host("a")
            .query(request.body.readUtf8())
            .build()
            .run { queryParameterNames().associateWith { queryParameter(it) } }.toMap()
        assertThat(postParams, allOf(aMapWithSize(2), hasEntry(PARAM1, VALUE3), hasEntry(PARAM2, VALUE4)))
    }

    @Test
    fun verifyPostRequestWithoutPostParams() {
        server.enqueue(MockResponse())
        val url = server.url("/")

        val params = ConnectionParameters.ParameterBuilder()
            .setRequestMethod(ConnectionParameters.RequestMethod.POST)
            .setRequestProperties(HEADERS)
            .create()
        client.connect(Uri.parse(url.toString()), params)
        assertEquals(1, server.requestCount)
        val request = server.takeRequest()
        assertEquals(url, request.requestUrl)
        assertEquals("POST", request.method)
        assertEquals(VALUE1, request.headers.get(HEADER1))
        assertEquals(VALUE2, request.headers.get(HEADER2))
        assertEquals(0, request.bodySize)
        assertEquals("", request.body.readUtf8())
    }
}
