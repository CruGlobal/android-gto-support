package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage

interface ChangeAwareTokenStorage : TokenStorage, TokenStorageObserverRegistry {
    companion object {
        fun TokenStorage.makeChangeAware(): ChangeAwareTokenStorage = when (this) {
            is ChangeAwareTokenStorage -> this
            else -> WrappedTokenStorage(this)
        }
    }
}
