package org.ccci.gto.android.common.okta.oidc.clients.sessions

import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.sessions.oktaState
import com.okta.oidc.clients.sessions.syncSessionClient
import com.okta.oidc.oktaRepo
import com.okta.oidc.storage.storage
import org.ccci.gto.android.common.okta.oidc.oktaUserId
import org.ccci.gto.android.common.okta.oidc.parseIdToken

internal val SessionClient.oktaRepo get() = syncSessionClient!!.oktaState!!.oktaRepo!!
internal val SessionClient.oktaStorage get() = oktaRepo.storage!!

val SessionClient.idToken get() = tokens?.idToken?.parseIdToken()
val SessionClient.oktaUserId get() = tokens?.oktaUserId
