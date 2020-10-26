package org.ccci.gto.android.common.okta.oidc

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.util.AuthorizationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.ccci.gto.android.common.okta.oidc.clients.sessions.changeFlow
import org.ccci.gto.android.common.okta.oidc.clients.sessions.getUserProfile
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaRepo
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaUserIdFlow
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo
import org.ccci.gto.android.common.okta.oidc.storage.getPersistableUserInfo

@VisibleForTesting
internal const val RETRIEVED_AT = "retrieved_at"

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalCoroutinesApi::class)
class OktaUserProfileProvider(private val sessionClient: SessionClient) {
    private val oktaRepo = sessionClient.oktaRepo

    fun userInfoFlow() = sessionClient.oktaUserIdFlow()
        .flatMapLatest { it?.let { userInfoFlow(it) } ?: flowOf(null) }
        .conflate()

    fun userInfoFlow(oktaUserId: String) = sessionClient.changeFlow()
        .map { oktaRepo.getPersistableUserInfo(oktaUserId)?.userInfo?.takeIf { it["sub"] == oktaUserId } }
        .conflate()

    private suspend fun load() {
        try {
            val profile = sessionClient.getUserProfile()
            profile.raw?.put(RETRIEVED_AT, System.currentTimeMillis())
            profile["sub"]?.toString()?.let { oktaRepo.save(PersistableUserInfo(it, profile)) }
        } catch (e: AuthorizationException) {
        }
    }
}
