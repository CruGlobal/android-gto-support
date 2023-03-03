package org.ccci.gto.android.common.facebook.login

import com.facebook.AccessToken
import com.facebook.AccessTokenManager
import com.facebook.AccessTokenTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transformLatest

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

@OptIn(ExperimentalCoroutinesApi::class)
fun AccessTokenManager.isAuthenticatedFlow() = currentAccessTokenFlow()
    .transformLatest {
        while (it?.isExpired == false) {
            emit(true)
            delay((it.expires.time - System.currentTimeMillis()).coerceAtLeast(1))
        }
        emit(false)
    }
    .distinctUntilChanged()
