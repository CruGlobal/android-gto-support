package org.ccci.gto.android.common.okta.oidc.clients.sessions

import com.okta.oidc.OktaIdToken
import com.okta.oidc.RequestCallback
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.util.AuthorizationException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.changeFlow

suspend fun SessionClient.getUserProfile(): UserInfo = suspendCoroutine { cont ->
    getUserProfile(object : RequestCallback<UserInfo, AuthorizationException> {
        override fun onSuccess(result: UserInfo) = cont.resumeWith(Result.success(result))
        override fun onError(error: String?, exception: AuthorizationException) =
            cont.resumeWith(Result.failure(exception))
    })
}

suspend fun SessionClient.refreshToken(): Tokens = suspendCoroutine { cont ->
    refreshToken(object : RequestCallback<Tokens, AuthorizationException> {
        override fun onSuccess(result: Tokens) = cont.resumeWith(Result.success(result))
        override fun onError(error: String?, exception: AuthorizationException) =
            cont.resumeWith(Result.failure(exception))
    })
}

internal fun SessionClient.changeFlow() = (oktaStorage as ChangeAwareOktaStorage).changeFlow()

private fun SessionClient.tokensFlow(): Flow<Tokens?> = changeFlow().map { tokens }.conflate()
fun SessionClient.idTokenFlow() =
    tokensFlow().map { it?.idToken }.distinctUntilChanged().map { it?.let { OktaIdToken.parseIdToken(it) } }.conflate()
fun SessionClient.oktaUserIdFlow() = idTokenFlow().map { it?.claims?.sub }.distinctUntilChanged().conflate()
