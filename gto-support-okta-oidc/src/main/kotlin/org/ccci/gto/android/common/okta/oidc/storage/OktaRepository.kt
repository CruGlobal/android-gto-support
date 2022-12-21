@file:SuppressLint("RestrictedApi")

package org.ccci.gto.android.common.okta.oidc.storage

import android.annotation.SuppressLint
import com.okta.oidc.storage.OktaRepository
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo

internal fun OktaRepository.getPersistableUserInfo(oktaUserId: String): PersistableUserInfo? =
    get(PersistableUserInfo.Restore(oktaUserId))
