@file:JvmName("Transformations2")

package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 * Returning either of the original LiveData objects will cause an Exception.
 */
@JvmName("combine")
fun <X, Y, Z> LiveData<X>.combineWith(other: LiveData<Y>, mapFunction: (X?, Y?) -> LiveData<Z>): LiveData<Z> {
    val result = MediatorLiveData<Z>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<Z>? = null
        override fun onChanged(t: Any?) {
            val newSource = mapFunction(value, other.value)
            if (source == newSource) return
            source?.let { result.removeSource(it) }
            source = newSource
            source?.let { result.addSource(it) { value: Z? -> result.value = value } }
        }
    }
    result.addSource(this, observer)
    result.addSource(other, observer)
    return result
}
