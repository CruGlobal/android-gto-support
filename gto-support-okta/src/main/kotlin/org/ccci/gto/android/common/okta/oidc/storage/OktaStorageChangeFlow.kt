package org.ccci.gto.android.common.okta.oidc.storage

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

@OptIn(ExperimentalCoroutinesApi::class)
internal fun ChangeAwareOktaStorage.changeFlow() = callbackFlow {
    val observer = object : ChangeAwareOktaStorage.Observer {
        override fun onChanged() {
            trySend(Unit)
        }
    }
    addObserver(observer)
    trySend(Unit)
    awaitClose { removeObserver(observer) }
}.conflate()
