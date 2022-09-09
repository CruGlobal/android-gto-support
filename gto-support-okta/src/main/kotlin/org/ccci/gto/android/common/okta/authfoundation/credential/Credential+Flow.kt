package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.Credential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun Credential.isAuthenticatedFlow() = getTokenFlow().map { it != null }.distinctUntilChanged()
fun Credential.idTokenFlow(): Flow<Any?> = getTokenFlow().map { idToken() }.distinctUntilChanged()
