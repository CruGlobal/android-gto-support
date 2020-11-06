package com.okta.oidc.clients.sessions

import com.okta.oidc.OktaState
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val implOktaStateField by lazy { getDeclaredFieldOrNull<SyncSessionClientImpl>("mOktaState") }

internal val SyncSessionClient.oktaState
    get() = when (this) {
        is SyncSessionClientImpl -> implOktaStateField?.getOrNull<OktaState>(this)
        else -> null
    }
