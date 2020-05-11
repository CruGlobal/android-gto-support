@file:JvmName("Transformations2")

package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 * Returning either of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <IN1, IN2, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    mapFunction: (IN1?, IN2?) -> LiveData<out OUT>
) = switchCombineWithInt(this, other) { mapFunction(value, other.value) }

/**
 * This method will combine 3 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of the source LiveData objects.
 * Returning any of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <IN1, IN2, IN3, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    mapFunction: (IN1?, IN2?, IN3?) -> LiveData<out OUT>
) = switchCombineWithInt(this, other, other2) { mapFunction(value, other.value, other2.value) }

/**
 * This method will combine 4 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of the source LiveData objects.
 * Returning any of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <IN1, IN2, IN3, IN4, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    mapFunction: (IN1?, IN2?, IN3?, IN4?) -> LiveData<out OUT>
) = switchCombineWithInt(this, other, other2, other3) { mapFunction(value, other.value, other2.value, other3.value) }

private inline fun <OUT> switchCombineWithInt(
    vararg input: LiveData<*>,
    crossinline mapFunction: () -> LiveData<out OUT>
): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<out OUT>? = null
        override fun onChanged(t: Any?) {
            val newSource = mapFunction()
            if (source == newSource) return
            source?.let { result.removeSource(it) }
            source = newSource
            source?.let { result.addSource(it, result::setValue) }
        }
    }
    input.forEach { result.addSource(it, observer) }
    return result
}

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmName("combine")
fun <IN1, IN2, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    mapFunction: (IN1?, IN2?) -> OUT
) = combineWithInt(this, other) { mapFunction(value, other.value) }

private inline fun <OUT> combineWithInt(
    vararg input: LiveData<*>,
    crossinline mapFunction: () -> OUT
): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = Observer<Any?> { result.value = mapFunction() }
    input.forEach { result.addSource(it, observer) }
    return result
}

fun <T> LiveData<out Iterable<T>>.sortedWith(comparator: Comparator<in T>) = map { it.sortedWith(comparator) }
