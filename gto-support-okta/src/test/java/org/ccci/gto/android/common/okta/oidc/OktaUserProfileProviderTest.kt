package org.ccci.gto.android.common.okta.oidc

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.OktaStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.makeChangeAware
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class OktaUserProfileProviderTest : BaseOktaOidcTest() {
    private lateinit var sessionClient: SessionClient
    private val innerStorage: OktaStorage = mock()
    override val storage: ChangeAwareOktaStorage = innerStorage.makeChangeAware() as ChangeAwareOktaStorage
    private lateinit var testScope: TestCoroutineScope
    private lateinit var tokens: Tokens

    private lateinit var provider: OktaUserProfileProvider

    @Before
    fun setup() {
        tokens = mock { on { idToken } doReturn ID_TOKEN }
        sessionClient = spy(webAuthClient.sessionClient) {
            doReturn(tokens).whenever(it).tokens
        }
        testScope = TestCoroutineScope()
        testScope.pauseDispatcher()
        provider = OktaUserProfileProvider(sessionClient, testScope)
    }

    @After
    fun cleanup() {
        provider.shutdown()
    }

    @Test
    fun verifyUserInfoFlow() = runBlockingTest {
        val userInfoField = "PersistableUserInfo:$OKTA_USER_ID"
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        verify(innerStorage).get(userInfoField)
        assertEquals(1, results.size)
        assertNull(results[0])

        // update user info
        results.clear()
        reset(innerStorage)
        innerStorage.stub {
            on { get(userInfoField) } doReturn """
                {
                    sub: "1234567890",
                    field: "test",
                    $RETRIEVED_AT: ${System.currentTimeMillis()}
                }
            """
        }
        storage.notifyChanged()
        verify(innerStorage).get(userInfoField)
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!["sub"])
        assertEquals("test", results[0]!!["field"])

        // close the flow
        flow.cancel()
    }

    @Test
    fun verifyUserInfoFlowChangeUser() = runBlockingTest {
        val userInfoField = "PersistableUserInfo:$OKTA_USER_ID"
        tokens.stub { on { idToken } doReturn null }
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        verify(innerStorage, never()).get(userInfoField)
        assertEquals(1, results.size)
        assertNull(results[0])

        // update user info for different oktaUserId
        results.clear()
        innerStorage.stub {
            on { get(userInfoField) } doReturn """
                {
                    sub: "1234567890",
                    field: "test",
                    $RETRIEVED_AT: ${System.currentTimeMillis()}
                }
            """
        }
        storage.notifyChanged()
        verify(innerStorage, never()).get(userInfoField)
        assertEquals(0, results.size)

        // update user id
        tokens.stub { on { idToken } doReturn ID_TOKEN }
        storage.notifyChanged()
        verify(innerStorage).get(userInfoField)
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!["sub"])
        assertEquals("test", results[0]!!["field"])

        // close the flow
        flow.cancel()
    }
}
