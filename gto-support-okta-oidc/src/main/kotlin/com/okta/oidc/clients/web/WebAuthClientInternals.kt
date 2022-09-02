package com.okta.oidc.clients.web

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val implSyncAuthClientField by lazy { getDeclaredFieldOrNull<WebAuthClientImpl>("mSyncAuthClient") }

internal val WebAuthClient.syncAuthClient
    get() = when (this) {
        is WebAuthClientImpl -> implSyncAuthClientField?.getOrNull<SyncWebAuthClient>(this)
        else -> null
    }
