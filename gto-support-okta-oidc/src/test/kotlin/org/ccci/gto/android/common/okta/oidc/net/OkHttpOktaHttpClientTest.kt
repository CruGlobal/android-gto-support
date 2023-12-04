package org.ccci.gto.android.common.okta.oidc.net

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.net.ConnectionParameters
import com.okta.oidc.net.request.HttpRequestBuilder
import com.okta.oidc.net.request.ProviderConfiguration
import com.okta.oidc.util.AuthorizationException
import java.net.InetAddress
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasEntry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

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
    private val cert = HeldCertificate.Builder()
        .addSubjectAlternativeName(InetAddress.getByName("localhost").canonicalHostName)
        .build()
    private val certs = HandshakeCertificates.Builder()
        .addTrustedCertificate(cert.certificate)
        .heldCertificate(cert)
        .build()

    @get:Rule
    val server = MockWebServer().apply {
        useHttps(certs.sslSocketFactory(), false)
    }
    private val okhttp = OkHttpClient.Builder()
        .sslSocketFactory(certs.sslSocketFactory(), certs.trustManager)
        .build()

    private val client = OkHttpOktaHttpClient(okhttp)

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
            .run { queryParameterNames.associateWith { queryParameter(it) } }.toMap()
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

    // region RefreshTokenRequest
    private fun refreshTokenRequest() = HttpRequestBuilder.newRefreshTokenRequest()
        .tokenResponse(
            mock {
                on { refreshToken } doReturn "refresh_token"
                on { scope } doReturn "email"
            }
        )
        .providerConfiguration(ProviderConfiguration().apply { token_endpoint = server.url("/token").toString() })
        .config(mock { on { clientId } doReturn "" })
        .createRequest()

    @Test
    fun testRefreshTokenRequest() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"access_token":"success","token_type":"bearer","expires_in":300}""")
        )

        val response = refreshTokenRequest().executeRequest(client)
        assertEquals("success", response.accessToken)
    }

    @Test
    fun `testRefreshTokenRequest - invalid_client`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "invalid_client"}""")
        )

        try {
            refreshTokenRequest().executeRequest(client)
        } catch (e: AuthorizationException) {
            assertEquals(AuthorizationException.TokenRequestErrors.INVALID_CLIENT, e)
        }
    }

    @Test
    fun `testRefreshTokenRequest - invalid_grant`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("""{"error": "invalid_grant"}""")
        )

        try {
            refreshTokenRequest().executeRequest(client)
        } catch (e: AuthorizationException) {
            assertEquals(AuthorizationException.TokenRequestErrors.INVALID_GRANT, e)
        }
    }
    // endregion RefreshTokenRequest
}
