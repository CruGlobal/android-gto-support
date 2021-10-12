package org.ccci.gto.android.common.okta.oidc.clients.sessions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import org.ccci.gto.android.common.okta.oidc.ID_TOKEN
import org.ccci.gto.android.common.okta.oidc.OKTA_USER_ID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionClientTest {
    private lateinit var sessionClient: SessionClient
    private lateinit var tokens: Tokens

    @Before
    fun setup() {
        tokens = mock { on { idToken } doReturn ID_TOKEN }
        sessionClient = mock { on { tokens } doReturn tokens }
    }

    @Test
    fun verifyIdTokenNullTokens() {
        whenever(sessionClient.tokens).thenReturn(null)
        assertNull(sessionClient.idToken)
    }

    @Test
    fun verifyIdTokenNullIdToken() {
        whenever(tokens.idToken).thenReturn(null)
        assertNull(sessionClient.idToken)
    }

    @Test
    fun verifyIdToken() {
        val idToken = sessionClient.idToken
        assertNotNull(idToken)
    }

    @Test
    fun verifyOktaUserId() {
        assertEquals(OKTA_USER_ID, sessionClient.oktaUserId)
    }
}
