package org.ccci.gto.android.common.okta.oidc

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.storage.OktaRepository
import com.okta.oidc.util.AuthorizationException
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import org.ccci.gto.android.common.base.TimeConstants.MIN_IN_MS
import org.ccci.gto.android.common.okta.oidc.clients.sessions.changeFlow
import org.ccci.gto.android.common.okta.oidc.clients.sessions.getUserProfile
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaRepo
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaUserId
import org.ccci.gto.android.common.okta.oidc.clients.sessions.oktaUserIdFlow
import org.ccci.gto.android.common.okta.oidc.clients.sessions.refreshToken
import org.ccci.gto.android.common.okta.oidc.clients.sessions.tokensSafe
import org.ccci.gto.android.common.okta.oidc.net.response.PersistableUserInfo
import org.ccci.gto.android.common.okta.oidc.net.response.oktaUserId
import org.ccci.gto.android.common.okta.oidc.storage.getPersistableUserInfo

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class, ObsoleteCoroutinesApi::class)
class OktaUserProfileProvider @VisibleForTesting internal constructor(
    private val sessionClient: SessionClient,
    private val oktaRepo: OktaRepository = sessionClient.oktaRepo,
    coroutineScope: CoroutineScope,
) {
    constructor(sessionClient: SessionClient) : this(sessionClient, coroutineScope = CoroutineScope(Dispatchers.IO))

    @VisibleForTesting
    internal val activeFlows = AtomicInteger(0)
    @VisibleForTesting
    internal val refreshIfStaleFlows = AtomicInteger(0)

    fun userInfoFlow(refreshIfStale: Boolean = true) = sessionClient.oktaUserIdFlow()
        .flatMapLatest { it?.let { userInfoFlow(it, refreshIfStale) } ?: flowOf(null) }
        .conflate()

    fun userInfoFlow(oktaUserId: String, refreshIfStale: Boolean = true) = sessionClient.changeFlow()
        .map { oktaRepo.getPersistableUserInfo(oktaUserId)?.userInfo?.takeIf { it.oktaUserId == oktaUserId } }
        .onStart {
            activeFlows.incrementAndGet()
            if (refreshIfStale) refreshIfStaleFlows.incrementAndGet()
            refreshActor.send(Unit)
        }
        .onCompletion {
            if (refreshIfStale) refreshIfStaleFlows.decrementAndGet()
            activeFlows.decrementAndGet()
        }
        .conflate()

    @VisibleForTesting
    internal val refreshActor = coroutineScope.actor<Unit>(capacity = CONFLATED) {
        val oktaUserIdChannel = sessionClient.oktaUserIdFlow().produceIn(this)

        try {
            while (!channel.isClosedForSend && !channel.isClosedForReceive && !oktaUserIdChannel.isClosedForReceive) {
                // load user info if there are active flows and user info hasn't been loaded yet or is stale
                val hasActiveFlows = activeFlows.get() > 0
                val refreshIfStale = refreshIfStaleFlows.get() > 0
                val userId = if (hasActiveFlows) sessionClient.oktaUserId else null
                val userInfo = userId?.let { oktaRepo.getPersistableUserInfo(it) }
                if (hasActiveFlows && userId != null && (userInfo == null || (userInfo.isStale && refreshIfStale))) {
                    loadUserProfile()
                }

                // suspend until we need to reload the profile
                select {
                    channel.onReceiveCatching {}

                    // enable other monitors when there are active flows
                    if (hasActiveFlows) {
                        oktaUserIdChannel.onReceiveCatching {}

                        if (userId != null && refreshIfStale) {
                            onTimeout((userInfo?.nextRefreshDelay ?: 0).coerceAtLeast(MIN_IN_MS)) {}
                        }
                    }
                }
            }
        } finally {
            oktaUserIdChannel.cancel()
        }
    }

    private suspend fun loadUserProfile() {
        if (!sessionClient.isAuthenticated) return
        try {
            if (sessionClient.tokensSafe?.isAccessTokenExpired != false) sessionClient.refreshToken()
            val profile = sessionClient.getUserProfile()
            profile.oktaUserId?.let { oktaRepo.save(PersistableUserInfo(it, profile)) }
        } catch (e: AuthorizationException) {
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun shutdown() {
        refreshActor.close()
    }
}
