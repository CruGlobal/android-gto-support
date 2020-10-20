package org.ccci.gto.android.common.okta.oidc.storage

import com.okta.oidc.storage.OktaStorage

internal interface ChangeAwareOktaStorage : OktaStorage {
    val observerRegistry: MutableList<Observer>

    fun addObserver(observer: Observer) {
        with(observerRegistry) { synchronized(this) { if (!contains(observer)) add(observer) } }
    }

    fun removeObserver(observer: Observer) {
        with(observerRegistry) { synchronized(this) { remove(observer) } }
    }

    fun notifyChanged() {
        with(observerRegistry) { synchronized(this) { toList() } }.forEach { it.onChanged() }
    }

    interface Observer {
        fun onChanged()
    }
}

fun OktaStorage.makeChangeAware(): OktaStorage = when (this) {
    is ChangeAwareOktaStorage -> this
    else -> WrappedOktaStorage(this)
}
