package org.ccci.gto.android.common.okta.oidc

import com.nhaarman.mockitokotlin2.mock
import com.okta.oidc.Okta
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.storage.OktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.security.NoopEncryptionManager
import org.junit.Before

abstract class BaseOktaOidcTest(protected open val storage: OktaStorage = mock()) {
    protected lateinit var webAuthClient: WebAuthClient

    @Before
    fun buildOktaClient() {
        webAuthClient = Okta.WebAuthBuilder()
            .setCacheMode(false)
            .withEncryptionManager(NoopEncryptionManager)
            .withStorage(storage)
            .create()
    }
}
