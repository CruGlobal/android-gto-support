package org.ccci.gto.android.common.okta.oidc

import com.okta.oidc.Okta
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.OktaHttpClient
import com.okta.oidc.storage.OktaStorage
import kotlinx.coroutines.test.TestScope
import org.ccci.gto.android.common.okta.oidc.storage.security.NoopEncryptionManager
import org.junit.Before
import org.mockito.kotlin.mock

abstract class BaseOktaOidcTest(
    protected open val httpClient: OktaHttpClient = mock(),
    protected open val storage: OktaStorage = mock(),
) {
    protected val testScope = TestScope()
    protected lateinit var webAuthClient: WebAuthClient

    @Before
    fun buildOktaClient() {
        webAuthClient = Okta.WebAuthBuilder()
            .setCacheMode(false)
            .withOktaHttpClient(httpClient)
            .withEncryptionManager(NoopEncryptionManager)
            .withStorage(storage)
            .create()
    }
}
