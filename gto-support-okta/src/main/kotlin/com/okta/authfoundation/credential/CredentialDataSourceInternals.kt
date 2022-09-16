package com.okta.authfoundation.credential

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val storageField by lazy { getDeclaredFieldOrNull<CredentialDataSource>("storage") }

internal val CredentialDataSource.storage get() = storageField?.getOrNull<TokenStorage>(this)
