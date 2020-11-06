package com.okta.oidc.storage

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val storageField by lazy { getDeclaredFieldOrNull<OktaRepository>("storage") }

internal val OktaRepository.storage get() = storageField?.getOrNull<OktaStorage>(this)
