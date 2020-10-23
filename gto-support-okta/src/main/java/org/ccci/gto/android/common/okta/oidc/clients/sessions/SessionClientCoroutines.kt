package org.ccci.gto.android.common.okta.oidc.clients.sessions

import com.okta.oidc.OktaIdToken
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.changeFlow

internal fun SessionClient.changeFlow() = (oktaStorage as ChangeAwareOktaStorage).changeFlow()

private fun SessionClient.tokensFlow(): Flow<Tokens?> = changeFlow().map { tokens }
fun SessionClient.idTokenFlow() =
    tokensFlow().map { it?.idToken }.distinctUntilChanged().map { it?.let { OktaIdToken.parseIdToken(it) } }
