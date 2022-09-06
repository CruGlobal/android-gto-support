package org.ccci.gto.android.common.okta.authfoundation.credential

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

internal fun ChangeAwareTokenStorage.changeFlow() = callbackFlow {
    val observer = object : TokenStorageObserverRegistry.Observer {
        override fun onChanged(id: String) {
            trySend(Unit)
        }
    }
    addObserver(observer)
    trySend(Unit)
    awaitClose { removeObserver(observer) }
}.conflate()
