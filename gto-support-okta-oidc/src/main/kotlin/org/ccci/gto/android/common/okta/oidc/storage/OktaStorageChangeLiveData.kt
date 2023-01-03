package org.ccci.gto.android.common.okta.oidc.storage

import androidx.lifecycle.MutableLiveData

internal class OktaStorageChangeLiveData(
    private val oktaStorage: ChangeAwareOktaStorage,
) : MutableLiveData<Unit>(Unit), ChangeAwareOktaStorage.Observer {
    override fun onActive() {
        super.onActive()
        oktaStorage.addObserver(this)
        postValue(Unit)
    }

    override fun onInactive() {
        super.onInactive()
        oktaStorage.removeObserver(this)
    }

    override fun onChanged() {
        postValue(Unit)
    }
}

internal inline val ChangeAwareOktaStorage.changeLiveData get() = OktaStorageChangeLiveData(this)
