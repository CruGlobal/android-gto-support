package org.ccci.gto.android.common.okta.oidc.clients.sessions

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.storage.OktaStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.ccci.gto.android.common.okta.oidc.BaseOktaOidcTest
import org.ccci.gto.android.common.okta.oidc.ID_TOKEN
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.makeChangeAware
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SessionClientCoroutinesTest : BaseOktaOidcTest() {
    override val storage = mockk<OktaStorage>().makeChangeAware() as ChangeAwareOktaStorage
    private val tokens: Tokens = mockk {
        every { idToken } returns null
    }

    private lateinit var sessionClient: SessionClient

    @Before
    fun setupSessionClient() {
        sessionClient = spyk(webAuthClient.sessionClient) {
            every { tokens } returns this@SessionClientCoroutinesTest.tokens
        }
    }

    @Test
    fun `changeFlow()`() = testScope.runTest {
        sessionClient.changeFlow().test {
            awaitItem()

            storage.notifyChanged()
            awaitItem()
        }
    }

    @Test
    fun `idTokenFlow()`() = testScope.runTest {
        sessionClient.idTokenFlow().test {
            assertNull(awaitItem())

            every { tokens.idToken } returns ID_TOKEN
            storage.notifyChanged()
            assertNotNull(awaitItem())
        }
    }
}
