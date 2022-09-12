package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.claims.subject
import com.okta.authfoundation.credential.Credential

val Credential.isAuthenticated get() = token != null
suspend fun Credential.getOktaUserId() = idToken()?.subject
