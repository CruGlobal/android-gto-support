package org.ccci.gto.android.common.okta.oidc.clients.sessions

import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.okta.oidc.clients.sessions.SessionClient
import org.ccci.gto.android.common.okta.oidc.storage.ChangeAwareOktaStorage
import org.ccci.gto.android.common.okta.oidc.storage.changeLiveData

private inline val SessionClient.changeLiveData get() = (oktaStorage as ChangeAwareOktaStorage).changeLiveData

val SessionClient.isAuthenticatedLiveData get() = changeLiveData.map { isAuthenticated }.distinctUntilChanged()
