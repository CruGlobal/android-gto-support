package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

fun <T> SavedStateHandle.delegate(key: String? = null) = object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? = get(key ?: property.name)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) = set(key ?: property.name, value)
}

fun <T : Any> SavedStateHandle.delegate(key: String? = null, ifNull: T) = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T = get(key ?: property.name) ?: ifNull
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = set(key ?: property.name, value)
}

fun <T> SavedStateHandle.livedata(key: String? = null) = ReadOnlyProperty<Any, MutableLiveData<T>> { _, property ->
    getLiveData(key ?: property.name)
}

fun <T> SavedStateHandle.livedata(key: String? = null, initialValue: T) =
    ReadOnlyProperty<Any, MutableLiveData<T>> { _, property ->
        getLiveData(key ?: property.name, initialValue)
    }

fun <T> SavedStateHandle.stateFlow(scope: CoroutineScope, key: String? = null) = stateFlow<T?>(scope, key, null)
fun <T> SavedStateHandle.stateFlow(scope: CoroutineScope, key: String? = null, initialValue: T) =
    ReadOnlyProperty<Any, MutableStateFlow<T>> { _, property ->
        getStateFlow(scope, key ?: property.name, initialValue)
    }
