package org.ccci.gto.android.common.okta.oidc.storage

import com.okta.oidc.storage.OktaStorage

internal class WrappedOktaStorage(private val storage: OktaStorage) : ChangeAwareOktaStorage {
    override val observerRegistry = mutableListOf<ChangeAwareOktaStorage.Observer>()

    override fun save(key: String, value: String) {
        storage.save(key, value)
        notifyChanged()
    }

    override fun get(key: String) = storage.get(key)

    override fun delete(key: String) {
        storage.delete(key)
        notifyChanged()
    }
}
