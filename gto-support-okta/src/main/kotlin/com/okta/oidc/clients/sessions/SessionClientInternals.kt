package com.okta.oidc.clients.sessions

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val implSyncSessionClientField by lazy { getDeclaredFieldOrNull<SessionClientImpl>("mSyncSessionClient") }

internal val SessionClient.syncSessionClient
    get() = when (this) {
        is SessionClientImpl -> implSyncSessionClientField?.getOrNull<SyncSessionClient>(this)
        else -> null
    }
