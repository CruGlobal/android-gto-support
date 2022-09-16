package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage

internal class WrappedTokenStorage(private val delegate: TokenStorage) :
    ChangeAwareTokenStorage,
    TokenStorageObserverRegistry by DefaultTokenStorageObserverRegistry() {
    override suspend fun entries() = delegate.entries()

    override suspend fun add(id: String) {
        delegate.add(id)
        notifyChanged(id)
    }

    override suspend fun replace(updatedEntry: TokenStorage.Entry) {
        delegate.replace(updatedEntry)
        notifyChanged(updatedEntry.identifier)
    }

    override suspend fun remove(id: String) {
        delegate.remove(id)
        notifyChanged(id)
    }
}
