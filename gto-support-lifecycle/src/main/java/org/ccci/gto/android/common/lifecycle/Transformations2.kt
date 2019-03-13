@file:JvmName("Transformations2")

package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

fun <X, Y, Z> combine(first: LiveData<X>, second: LiveData<Y>, block: (X?, Y?) -> LiveData<Z>): LiveData<Z> {
    val result = MediatorLiveData<Z>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<Z>? = null
        override fun onChanged(t: Any?) {
            val newSource = block(first.value, second.value)
            if (source == newSource) return
            source?.let { result.removeSource(it) }
            source = newSource
            source?.let { result.addSource(it) { value: Z? -> result.value = value } }
        }
    }
    result.addSource(first, observer)
    result.addSource(second, observer)
    return result
}
