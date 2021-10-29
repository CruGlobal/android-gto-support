package org.ccci.gto.android.common.okta.oidc

import com.okta.oidc.Tokens

val Tokens.oktaUserId get() = idToken?.parseIdToken()?.oktaUserId
