package org.ccci.gto.android.common.okta.authfoundation.credential

internal class DefaultTokenStorageObserverRegistry : TokenStorageObserverRegistry {
    private val observerRegistry = mutableListOf<TokenStorageObserverRegistry.Observer>()

    override fun addObserver(observer: TokenStorageObserverRegistry.Observer) {
        with(observerRegistry) { synchronized(this) { if (!contains(observer)) add(observer) } }
    }

    override fun removeObserver(observer: TokenStorageObserverRegistry.Observer) {
        with(observerRegistry) { synchronized(this) { remove(observer) } }
    }

    override fun notifyChanged(id: String) {
        with(observerRegistry) { synchronized(this) { toList() } }.forEach { it.onChanged(id) }
    }
}
