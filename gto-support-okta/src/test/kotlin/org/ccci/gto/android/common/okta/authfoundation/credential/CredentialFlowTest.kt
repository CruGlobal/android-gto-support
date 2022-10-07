package org.ccci.gto.android.common.okta.authfoundation.credential

import app.cash.turbine.test
import com.okta.authfoundation.InternalAuthFoundationApi
import com.okta.authfoundation.claims.DefaultClaimsProvider
import com.okta.authfoundation.client.OidcClientResult
import com.okta.authfoundation.client.dto.OidcUserInfo
import com.okta.authfoundation.credential.Credential
import com.okta.authfoundation.credential.Token
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.ccci.gto.android.common.base.TimeConstants
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, InternalAuthFoundationApi::class)
class CredentialFlowTest {
    private val tokenFlow = MutableSharedFlow<Token?>(replay = 1).apply { tryEmit(null) }
    private var tags = emptyMap<String, String>()

    private val credential = mockk<Credential> {
        every { token } returns null
        every { tags } answers { this@CredentialFlowTest.tags }
        every { getTokenFlow() } returns tokenFlow
        coEvery { storeToken(token = any(), tags = any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            this@CredentialFlowTest.tags = it.invocation.args[1] as Map<String, String>
            tokenFlow.emit(null)
        }

        var responseCounter = 1
        coEvery { getUserInfo() }.coAnswers {
            delay(50)
            OidcClientResult.Success(oidcUserInfo(i = responseCounter++))
        }

        excludeRecords {
            token
            tags
            getTokenFlow()
        }
    }

    // region userInfoFlow()
    private val cachedUserInfo = """{"cached":true}"""
    private fun oidcUserInfo(i: Int = 1, rawJson: String = """{"response":$i}""") = OidcUserInfo(
        DefaultClaimsProvider(
            Json.decodeFromString(JsonObject.serializer(), rawJson),
            Json
        )
    )

    @Test
    fun `userInfoFlow() - cached`() = runTest {
        tags = mapOf(
            OIDC_USER_INFO to cachedUserInfo,
            OIDC_USER_INFO_LOAD_TIME to System.currentTimeMillis().toString()
        )

        credential.userInfoFlow().test {
            val cached = awaitItem()
            assertNotNull(cached)
            assertTrue(cached!!.deserializeClaim("cached", Boolean.serializer())!!)
            coVerify(exactly = 0) { credential.getUserInfo() }
        }
    }

    @Test
    fun `userInfoFlow() - cache stale`() = runTest {
        tags = mapOf(
            OIDC_USER_INFO to cachedUserInfo,
            OIDC_USER_INFO_LOAD_TIME to (System.currentTimeMillis() - TimeConstants.WEEK_IN_MS).toString()
        )

        credential.userInfoFlow().test {
            val cached = awaitItem()
            assertNotNull(
                "userInfoFlow() should emit any cached data while triggering a background load of getUserInfo()",
                cached
            )
            assertTrue(cached!!.deserializeClaim("cached", Boolean.serializer())!!)

            val response = awaitItem()
            assertNotNull(response)
            assertEquals(1, response!!.deserializeClaim("response", Int.serializer()))
            coVerify(exactly = 1) { credential.getUserInfo() }
            coVerify(exactly = 1) { credential.storeToken(tags = any(), token = any()) }
            assertThat(tags, hasEntry(OIDC_USER_INFO, """{"response":1}"""))
            confirmVerified(credential)
        }
    }

    @Test
    fun `userInfoFlow() - no cache`() = runTest {
        credential.userInfoFlow().test {
            val response = awaitItem()
            assertNotNull(
                "userInfoFlow() should wait for getUserInfo() to complete if there is no cached data",
                response
            )
            assertEquals(1, response!!.deserializeClaim("response", Int.serializer()))
            coVerify(exactly = 1) { credential.getUserInfo() }
            coVerify(exactly = 1) { credential.storeToken(tags = any(), token = any()) }
            assertThat(tags, hasEntry(OIDC_USER_INFO, """{"response":1}"""))
            confirmVerified(credential)
        }
    }

    @Test
    fun `userInfoFlow() - no cache & getUserInfo() fails`() = runTest {
        coEvery { credential.getUserInfo() } coAnswers { OidcClientResult.Error(Exception()) }

        credential.userInfoFlow().test {
            assertNull(
                "userInfoFlow() should emit null if there is no cached data and getUserInfo() fails",
                awaitItem()
            )
            coVerify { credential.getUserInfo() }
            coVerify(exactly = 0) { credential.storeToken(tags = any(), token = any()) }
            confirmVerified(credential)
        }
    }
    // endregion userInfoFlow()
}
