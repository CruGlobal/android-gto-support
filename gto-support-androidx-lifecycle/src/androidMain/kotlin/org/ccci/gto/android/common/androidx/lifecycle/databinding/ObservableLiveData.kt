package org.ccci.gto.android.common.androidx.lifecycle.databinding

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.LiveData
import kotlin.reflect.KProperty0

private class ObservablePropertyLiveData<T>(
    private val observable: Observable,
    private val property: KProperty0<T>,
    private vararg val propertyIds: Int
) : LiveData<T>() {
    private val listener = object : OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId == 0 || propertyIds.isEmpty() || propertyIds.contains(propertyId)) {
                value = property.get()
            }
        }
    }

    override fun onActive() {
        observable.addOnPropertyChangedCallback(listener)
        value = property.get()
    }

    override fun onInactive() = observable.removeOnPropertyChangedCallback(listener)
}

fun <T> Observable.getPropertyLiveData(property: KProperty0<T>, vararg propertyIds: Int): LiveData<T> =
    ObservablePropertyLiveData(this, property, *propertyIds)
