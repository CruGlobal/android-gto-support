package org.ccci.gto.android.common.okta.oidc

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.util.AuthorizationException
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout
import org.ccci.gto.android.common.okta.oidc.clients.sessions.changeFlow
import org.ccci.gto.android.common.okta.oidc.clients.sessions.getUserProfile
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaRepo
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaUserId
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaUserIdFlow
import org.ccci.gto.android.common.okta.oidc.clients.sessions.refreshToken
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo
import org.ccci.gto.android.common.okta.oidc.net.response.oktaUserId
import org.ccci.gto.android.common.okta.oidc.storage.getPersistableUserInfo

@VisibleForTesting
internal const val RETRIEVED_AT = "retrieved_at"

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class OktaUserProfileProvider @VisibleForTesting internal constructor(
    private val sessionClient: SessionClient,
    coroutineScope: CoroutineScope
) {
    constructor(sessionClient: SessionClient) : this(sessionClient, CoroutineScope(Dispatchers.IO))

    private val oktaRepo = sessionClient.oktaRepo

    private val activeFlows = AtomicInteger(0)

    fun userInfoFlow() = sessionClient.oktaUserIdFlow()
        .flatMapLatest { it?.let { userInfoFlow(it) } ?: flowOf(null) }
        .conflate()

    fun userInfoFlow(oktaUserId: String) = sessionClient.changeFlow()
        .map { oktaRepo.getPersistableUserInfo(oktaUserId)?.userInfo?.takeIf { it.oktaUserId == oktaUserId } }
        .onStart {
            activeFlows.incrementAndGet()
            refreshActor.offer(Unit)
        }
        .onCompletion { activeFlows.decrementAndGet() }
        .conflate()

    private val refreshActor = coroutineScope.actor<Unit>(capacity = CONFLATED) {
        while (true) {
            // suspend until there is an active flow
            if (activeFlows.get() <= 0) receive()

            // wait until a refresh is required (or the oktaUserId potentially changes)
            val userId = sessionClient.oktaUserId
            val userInfo = userId?.let { oktaRepo.getPersistableUserInfo(it) }?.takeUnless { it.isStale }
            userInfo?.nextRefreshTime
                ?.let { it - System.currentTimeMillis() }?.takeUnless { it <= 0 }
                ?.let { withTimeout(it) { receive() } }

            // short-circuit if there are no active flows, the oktaUserId changed, or the userInfo isn't stale
            if (activeFlows.get() <= 0) continue
            if (sessionClient.oktaUserId != userId) continue
            if (userInfo?.isStale == false) continue

            // trigger load
            load()
        }
    }

    private suspend fun load() {
        try {
            if (sessionClient.tokens?.isAccessTokenExpired != false) sessionClient.refreshToken()
            val profile = sessionClient.getUserProfile()
            profile.raw?.put(RETRIEVED_AT, System.currentTimeMillis())
            profile.oktaUserId?.let { oktaRepo.save(PersistableUserInfo(it, profile)) }
        } catch (e: AuthorizationException) {
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun shutdown() {
        refreshActor.close()
    }
}
