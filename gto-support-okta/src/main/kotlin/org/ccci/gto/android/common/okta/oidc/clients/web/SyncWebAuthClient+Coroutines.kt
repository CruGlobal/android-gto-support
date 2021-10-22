package org.ccci.gto.android.common.okta.oidc.clients.web

import android.app.Activity
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.web.SyncWebAuthClient
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun SyncWebAuthClient.signOutCoroutine(activity: Activity, flags: Int = BaseAuth.ALL): Int =
    suspendCancellableCoroutine {
        it.invokeOnCancellation { cancel() }
        try {
            it.resumeWith(Result.success(signOut(activity, flags)))
        } catch (e: Throwable) {
            it.resumeWith(Result.failure(e))
        }
    }
