package org.ccci.gto.android.common.okta.oidc.clients.sessions

import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.sessions.oktaState
import com.okta.oidc.clients.sessions.syncSessionClient
import com.okta.oidc.oktaRepo
import com.okta.oidc.storage.storage

internal val SessionClient.oktaStorage get() = syncSessionClient!!.oktaState!!.oktaRepo!!.storage!!
