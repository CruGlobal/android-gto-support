package org.ccci.gto.android.common.lifecycle.databinding

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.LiveData
import kotlin.reflect.KProperty0

private class ObservablePropertyLiveData<T>(
    private val observable: Observable,
    private val propertyId: Int,
    private val property: KProperty0<T>
) : LiveData<T>() {
    private val listener = object : OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            when (propertyId) {
                0, this@ObservablePropertyLiveData.propertyId -> value = property.get()
            }
        }
    }

    override fun onActive() {
        observable.addOnPropertyChangedCallback(listener)
        value = property.get()
    }

    override fun onInactive() = observable.removeOnPropertyChangedCallback(listener)
}

fun <T> Observable.getPropertyLiveData(propertyId: Int, property: KProperty0<T>): LiveData<T> =
    ObservablePropertyLiveData(this, propertyId, property)
