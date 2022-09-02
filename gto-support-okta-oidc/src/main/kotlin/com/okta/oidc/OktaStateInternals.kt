package com.okta.oidc

import com.okta.oidc.storage.OktaRepository
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getOrNull

private val oktaRepoField by lazy { getDeclaredFieldOrNull<OktaState>("mOktaRepo") }

internal val OktaState.oktaRepo get() = oktaRepoField?.getOrNull<OktaRepository>(this)
