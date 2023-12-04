package org.ccci.gto.android.common.okta.oidc.clients.web

import android.annotation.SuppressLint
import android.app.Activity
import com.okta.oidc.clients.AuthAPI
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.BaseAuth.REMOVE_TOKENS
import com.okta.oidc.clients.isCancelled
import com.okta.oidc.clients.resetCurrentStateInt
import com.okta.oidc.clients.web.SyncWebAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.ccci.gto.android.common.compat.app.registerActivityLifecycleCallbacksCompat
import org.ccci.gto.android.common.compat.app.unregisterActivityLifecycleCallbacksCompat
import org.ccci.gto.android.common.util.app.EmptyActivityLifecycleCallbacks
import splitties.bitflags.hasFlag

@SuppressLint("RestrictedApi")
suspend fun SyncWebAuthClient.signOutSuspending(activity: Activity, flags: Int = BaseAuth.ALL): Int {
    val callbacks = object : EmptyActivityLifecycleCallbacks() {
        override fun onActivityDestroyed(activity: Activity) = cancel()
    }
    try {
        activity.registerActivityLifecycleCallbacksCompat(callbacks)
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                it.invokeOnCancellation { cancel() }
                try {
                    // HACK: SyncWebAuthClientImpl doesn't always correctly reset the cancelled flag,
                    //       so manually reset it before calling signOut() if necessary
                    if (this is AuthAPI && isCancelled) resetCurrentStateInt()

                    it.resumeWith(Result.success(signOut(activity, flags)))
                } catch (e: Throwable) {
                    it.resumeWith(Result.failure(e))
                }
            }
        }
    } finally {
        if (flags.hasFlag(REMOVE_TOKENS)) withContext(Dispatchers.Default + NonCancellable) { sessionClient.clear() }
        activity.unregisterActivityLifecycleCallbacksCompat(callbacks)
    }
}
