@file:JvmName("Transformations2")

package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 * Returning either of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <IN1, IN2, OUT> LiveData<IN1>.switchCombineWith(other: LiveData<IN2>, mapFunction: (IN1?, IN2?) -> LiveData<OUT>?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<OUT>? = null
        override fun onChanged(t: Any?) {
            val newSource = mapFunction(value, other.value)
            if (source == newSource) return
            source?.let { result.removeSource(it) }
            source = newSource
            source?.let { result.addSource(it) { value: OUT? -> result.value = value } }
        }
    }
    result.addSource(this, observer)
    result.addSource(other, observer)
    return result
}

/**
 * This method will combine 3 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of the source LiveData objects.
 * Returning any of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <IN1, IN2, IN3, OUT> LiveData<IN1>.switchCombineWith(other: LiveData<IN2>, other2: LiveData<IN3>, mapFunction: (IN1?, IN2?, IN3?) -> LiveData<OUT>?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<OUT>? = null
        override fun onChanged(t: Any?) {
            val newSource = mapFunction(value, other.value, other2.value)
            if (source == newSource) return
            source?.let { result.removeSource(it) }
            source = newSource
            source?.let { result.addSource(it) { value: OUT? -> result.value = value } }
        }
    }
    result.addSource(this, observer)
    result.addSource(other, observer)
    result.addSource(other2, observer)
    return result
}

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmName("combine")
fun <IN1, IN2, OUT> LiveData<IN1>.combineWith(other: LiveData<IN2>, mapFunction: (IN1?, IN2?) -> OUT?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = Observer<Any?> { result.value = mapFunction(value, other.value) }
    result.addSource(this, observer)
    result.addSource(other, observer)
    return result
}

// Provide Kotlin extensions for existing transformations

fun <IN, OUT> LiveData<IN>.map(block: (IN?) -> OUT?): LiveData<OUT> = Transformations.map(this, block::invoke)
fun <IN, OUT> LiveData<IN>.flatMap(block: (IN?) -> LiveData<OUT>?): LiveData<OUT> =
    Transformations.switchMap(this, block::invoke)

fun <T> LiveData<out Iterable<T>>.sortedWith(comparator: Comparator<in T>) = map { it?.sortedWith(comparator) }
