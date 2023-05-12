package org.ccci.gto.android.common.okta.oidc

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.OktaRepository
import com.okta.oidc.storage.OktaStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.ccci.gto.android.common.base.TimeConstants.HOUR_IN_MS
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaRepo
import org.ccci.gto.android.common.okta.oidc.net.response.CLAIM_OKTA_USER_ID
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo
import org.ccci.gto.android.common.okta.oidc.net.response.oktaUserId
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.makeChangeAware
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class OktaUserProfileProviderTest : BaseOktaOidcTest() {
    private lateinit var sessionClient: SessionClient
    override val storage = mock<OktaStorage>().makeChangeAware() as ChangeAwareOktaStorage
    private lateinit var oktaRepo: OktaRepository
    private val testScope = TestScope()

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
        provider = OktaUserProfileProvider(sessionClient, oktaRepo, coroutineScope = testScope)
    }

    // region userInfoFlow()
    @Test
    fun verifyUserInfoFlow() = runTest(UnconfinedTestDispatcher()) {
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = provider.userInfoFlow().onEach { results += it }.launchIn(this)
        assertEquals(1, provider.activeFlows.get())
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
        assertEquals(0, provider.activeFlows.get())
    }

    @Test
    fun verifyUserInfoFlowChangeUser() = runTest(UnconfinedTestDispatcher()) {
        tokens.stub { on { idToken } doReturn null }
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = provider.userInfoFlow().onEach { results += it }.launchIn(this)
        assertEquals(0, provider.activeFlows.get())
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(1, results.size)
        assertNull(results[0])

        // update user info for different oktaUserId
        results.clear()
        userInfo.stub { on { userInfo } doReturn UserInfo(JSONObject(mapOf(CLAIM_OKTA_USER_ID to OKTA_USER_ID))) }
        storage.notifyChanged()
        assertEquals(0, provider.activeFlows.get())
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(0, results.size)

        // update user id
        tokens.stub { on { idToken } doReturn ID_TOKEN }
        storage.notifyChanged()
        assertEquals(1, provider.activeFlows.get())
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!.oktaUserId)

        // close the flow
        flow.cancel()
        assertEquals(0, provider.activeFlows.get())
    }

    @Test
    fun verifyUserInfoFlowNullOnLogout() = runTest(UnconfinedTestDispatcher()) {
        userInfo.stub { on { userInfo } doReturn UserInfo(JSONObject(mapOf(CLAIM_OKTA_USER_ID to OKTA_USER_ID))) }
        val results = mutableListOf<UserInfo?>()

        // initial user info
        val flow = launch { provider.userInfoFlow().collect { results += it } }
        assertEquals(1, provider.activeFlows.get())
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        assertEquals(1, results.size)
        assertEquals(OKTA_USER_ID, results[0]!!.oktaUserId)

        // user is logged out
        reset(oktaRepo)
        results.clear()
        tokens.stub { on { idToken } doReturn null }
        storage.notifyChanged()
        assertEquals(0, provider.activeFlows.get())
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        assertEquals(1, results.size)
        assertNull(results[0])

        // close the flow
        flow.cancel()
        assertEquals(0, provider.activeFlows.get())
    }
    // endregion userInfoFlow()

    // region refreshActor
    @Test
    fun verifyRefreshActorWakesUpOnNewFlow() = testScope.runTest {
        provider.activeFlows.set(0)
        advanceUntilIdle()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        verify(httpClient, never()).connect(any(), any())

        provider.activeFlows.set(1)
        provider.refreshActor.send(Unit)
        runCurrent()
        verify(oktaRepo, atLeastOnce()).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        verify(httpClient, never()).connect(any(), any())

        provider.shutdown()
    }

    @Test
    fun verifyRefreshActorWakesUpOnOktaUserIdChange() = testScope.runTest {
        tokens.stub { on { idToken } doReturn null }
        provider.activeFlows.set(1)
        provider.refreshActor.send(Unit)
        advanceUntilIdle()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        verify(httpClient, never()).connect(any(), any())

        tokens.stub { on { idToken } doReturn ID_TOKEN }
        storage.notifyChanged()
        runCurrent()
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        verify(httpClient, never()).connect(any(), any())

        provider.shutdown()
    }

    @Test
    fun verifyRefreshActorWakesUpWhenRefreshDelayHasExpired() = testScope.runTest {
        provider.activeFlows.set(1)
        provider.refreshIfStaleFlows.set(1)
        userInfo.stub {
            on { isStale } doReturn false
            on { nextRefreshDelay } doReturn HOUR_IN_MS
        }
        provider.refreshActor.send(Unit)

        runCurrent()
        verify(oktaRepo, atLeastOnce()).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        verify(httpClient, never()).connect(any(), any())

        clearInvocations(oktaRepo)
        advanceTimeBy(HOUR_IN_MS - 1)
        runCurrent()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        verify(httpClient, never()).connect(any(), any())

        advanceTimeBy(1)
        runCurrent()
        verify(oktaRepo).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        verify(httpClient, never()).connect(any(), any())

        provider.shutdown()
    }

    @Test
    fun verifyRefreshActorDoesntWakesUpForRefreshDelayWhenRefreshIfStaleIsFalse() = testScope.runTest {
        provider.activeFlows.set(1)
        provider.refreshIfStaleFlows.set(0)
        userInfo.stub {
            on { isStale } doReturn true
            on { nextRefreshDelay } doReturn -1
        }
        provider.refreshActor.send(Unit)

        testScope.runCurrent()
        verify(oktaRepo, atLeastOnce()).get(PersistableUserInfo.Restore(OKTA_USER_ID))
        verify(httpClient, never()).connect(any(), any())

        clearInvocations(oktaRepo)
        testScope.advanceUntilIdle()
        verify(oktaRepo, never()).get<PersistableUserInfo>(any())
        verify(httpClient, never()).connect(any(), any())

        provider.shutdown()
    }
    // endregion refreshActor
}
