package org.ccci.gto.android.common.okta.oidc

import com.okta.oidc.OktaIdToken

val OktaIdToken.oktaUserId get() = claims?.sub

internal fun String.parseIdToken(): OktaIdToken = OktaIdToken.parseIdToken(this)
