package org.ccci.gto.android.common.facebook.login

import com.facebook.AccessToken
import com.facebook.AccessTokenManager
import com.facebook.AccessTokenTracker
import com.facebook.FacebookException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun AccessTokenManager.currentAccessTokenFlow() = callbackFlow {
    val tracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
            trySendBlocking(currentAccessToken)
        }
    }
    tracker.startTracking()
    send(currentAccessToken)
    awaitClose { tracker.stopTracking() }
}.conflate()

@Deprecated("Since v4.2.0, use isExpiredFlow() instead.", ReplaceWith("isExpiredFlow()"))
fun AccessTokenManager.isAuthenticatedFlow() = isExpiredFlow().map { !it }

@OptIn(ExperimentalCoroutinesApi::class)
fun AccessTokenManager.isExpiredFlow() = currentAccessTokenFlow()
    .flatMapLatest { it?.isExpiredFlow() ?: flowOf(false) }
    .distinctUntilChanged()

suspend fun AccessTokenManager.refreshCurrentAccessToken() = suspendCoroutine { cont ->
    refreshCurrentAccessToken(
        object : AccessToken.AccessTokenRefreshCallback {
            override fun OnTokenRefreshed(accessToken: AccessToken?) = cont.resume(accessToken)

            override fun OnTokenRefreshFailed(exception: FacebookException?) = when (exception) {
                null -> cont.resume(null)
                else -> cont.resumeWithException(exception)
            }
        },
    )
}
