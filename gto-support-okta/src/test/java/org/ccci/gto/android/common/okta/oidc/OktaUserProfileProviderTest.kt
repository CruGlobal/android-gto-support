package org.ccci.gto.android.common.okta.oidc

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.OktaRepository
import com.okta.oidc.storage.OktaStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaRepo
import org.ccci.gto.android.common.okta.oidc.net.response.CLAIM_OKTA_USER_ID
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo
import org.ccci.gto.android.common.okta.oidc.net.response.oktaUserId
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.makeChangeAware
import org.json.JSONObject
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
    override val storage = mock<OktaStorage>().makeChangeAware() as ChangeAwareOktaStorage
    private lateinit var oktaRepo: OktaRepository
    private lateinit var testScope: TestCoroutineScope

    private lateinit var provider: OktaUserProfileProvider

    private lateinit var tokens: Tokens
    private lateinit var userInfo: PersistableUserInfo

    @Before
    fun setup() {
        tokens = mock { on { idToken } doReturn ID_TOKEN }
        userInfo = mock()

        sessionClient = spy(webAuthClient.sessionClient) { doReturn(tokens).whenever(it).tokens }
        oktaRepo = spy(sessionClient.oktaRepo) {
            doReturn(userInfo).whenever(it).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        }
        testScope = TestCoroutineScope()
        testScope.pauseDispatcher()
        provider = OktaUserProfileProvider(sessionClient, oktaRepo, coroutineScope = testScope)
    }

    @After
    fun cleanup() {
        provider.shutdown()
        testScope.cleanupTestCoroutines()
    }

    // region userInfoFlow()
    @Test
    fun verifyUserInfoFlow() = runBlockingTest {
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertNull(results[0])

        // update user info
        results.clear()
        userInfo.stub { on { userInfo } doReturn UserInfo(JSONObject(mapOf(CLAIM_OKTA_USER_ID to OKTA_USER_ID))) }
        storage.notifyChanged()
        verify(oktaRepo, times(2)).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!.oktaUserId)

        // close the flow
        flow.cancel()
    }

    @Test
    fun verifyUserInfoFlowChangeUser() = runBlockingTest {
        tokens.stub { on { idToken } doReturn null }
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(1, results.size)
        assertNull(results[0])

        // update user info for different oktaUserId
        results.clear()
        userInfo.stub { on { userInfo } doReturn UserInfo(JSONObject(mapOf(CLAIM_OKTA_USER_ID to OKTA_USER_ID))) }
        storage.notifyChanged()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(0, results.size)

        // update user id
        tokens.stub { on { idToken } doReturn ID_TOKEN }
        storage.notifyChanged()
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!.oktaUserId)

        // close the flow
        flow.cancel()
    }

    @Test
    fun verifyUserInfoFlowNullOnLogout() = runBlockingTest {
        userInfo.stub { on { userInfo } doReturn UserInfo(JSONObject(mapOf(CLAIM_OKTA_USER_ID to OKTA_USER_ID))) }
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!.oktaUserId)

        // user is logged out
        reset(oktaRepo)
        results.clear()
        tokens.stub { on { idToken } doReturn null }
        storage.notifyChanged()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(1, results.size)
        assertNull(results[0])

        // close the flow
        flow.cancel()
    }
    // endregion userInfoFlow()

    // region refreshActor
    @Test
    fun verifyRefreshActorSuspendsOnNoActiveFlows() {
        provider.activeFlows.set(0)
        testScope.resumeDispatcher()
        testScope.advanceUntilIdle()
        verify(sessionClient, never()).tokens
    }
    // endregion refreshActor
}
