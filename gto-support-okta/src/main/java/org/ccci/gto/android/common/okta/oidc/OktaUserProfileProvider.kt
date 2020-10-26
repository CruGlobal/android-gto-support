package org.ccci.gto.android.common.okta.oidc

import android.annotation.SuppressLint
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.Persistable
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
import org.json.JSONObject

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalCoroutinesApi::class)
class OktaUserProfileProvider(private val sessionClient: SessionClient) {
    private val oktaRepo = sessionClient.oktaRepo

    fun userInfoFlow() = sessionClient.oktaUserIdFlow()
        .flatMapLatest { it?.let { userInfoFlow(it) } ?: flowOf(null) }
        .conflate()

    fun userInfoFlow(oktaUserId: String) = sessionClient.changeFlow()
        .map { getPersistableUserInfo(oktaUserId)?.userInfo?.takeIf { it["sub"] == oktaUserId } }
        .conflate()

    private suspend fun load() {
        try {
            val profile = sessionClient.getUserProfile()
            profile["sub"]?.toString()?.let { oktaRepo.save(PersistableUserInfo(it, profile)) }
        } catch (e: AuthorizationException) {
        }
    }

    private fun getPersistableUserInfo(oktaId: String): PersistableUserInfo? =
        oktaRepo.get(PersistableUserInfo.restore(oktaId))

    private class PersistableUserInfo(val oktaId: String, val userInfo: UserInfo?, private val raw: String?) :
        Persistable {
        constructor(oktaId: String, userInfo: UserInfo?) : this(oktaId, userInfo, userInfo?.raw?.toString())
        constructor(oktaId: String, raw: String?) : this(oktaId, UserInfo(raw?.let { JSONObject(it) }), raw)

        override fun getKey() = buildKey(oktaId)
        override fun persist() = raw

        companion object {
            private fun buildKey(oktaId: String) = "PersistableUserInfo:$oktaId"

            fun restore(oktaId: String) = object : Persistable.Restore<PersistableUserInfo> {
                override fun getKey() = buildKey(oktaId)
                override fun restore(data: String?) = PersistableUserInfo(oktaId, data)
            }
        }
    }
}
