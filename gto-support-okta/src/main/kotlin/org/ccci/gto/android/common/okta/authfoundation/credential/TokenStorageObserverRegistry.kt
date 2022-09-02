package org.ccci.gto.android.common.okta.authfoundation.credential

interface TokenStorageObserverRegistry {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyChanged(id: String)

    interface Observer {
        fun onChanged(id: String)
    }
}
