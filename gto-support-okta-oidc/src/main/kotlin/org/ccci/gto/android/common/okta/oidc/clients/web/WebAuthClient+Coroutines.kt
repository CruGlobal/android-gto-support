package org.ccci.gto.android.common.okta.oidc.clients.web

import android.app.Activity
import com.okta.oidc.clients.BaseAuth
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.clients.web.syncAuthClient

suspend fun WebAuthClient.signOut(activity: Activity, flags: Int = BaseAuth.ALL) =
    syncAuthClient!!.signOutSuspending(activity, flags)
