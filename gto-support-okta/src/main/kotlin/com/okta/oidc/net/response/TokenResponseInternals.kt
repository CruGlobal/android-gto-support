package com.okta.oidc.net.response

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val expiresInField by lazy { getDeclaredFieldOrNull<TokenResponse>("expires_in") }

internal var TokenResponse.expires_in: String?
    get() = expiresInField?.getOrNull<String>(this)
    set(value) {
        expiresInField?.set(this, value)
    }
