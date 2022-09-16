package org.ccci.gto.android.common.okta.oidc.clients.sessions

import android.annotation.SuppressLint
import com.okta.oidc.Tokens
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.sessions.oktaState
import com.okta.oidc.clients.sessions.syncSessionClient
import com.okta.oidc.oktaRepo
import com.okta.oidc.storage.storage
import org.ccci.gto.android.common.okta.oidc.net.response.repair
import org.ccci.gto.android.common.okta.oidc.oktaUserId
import org.ccci.gto.android.common.okta.oidc.parseIdToken

internal val SessionClient.oktaRepo get() = syncSessionClient!!.oktaState!!.oktaRepo!!
internal val SessionClient.oktaStorage get() = oktaRepo.storage!!

@get:SuppressLint("RestrictedApi")
internal val SessionClient.tokensSafe
    get() = try {
        tokens
    } catch (e: NumberFormatException) {
        syncSessionClient!!.oktaState!!.tokenResponse?.repair()?.let { Tokens(it) }
    }

val SessionClient.idToken get() = tokensSafe?.idToken?.parseIdToken()
val SessionClient.oktaUserId get() = tokensSafe?.oktaUserId
