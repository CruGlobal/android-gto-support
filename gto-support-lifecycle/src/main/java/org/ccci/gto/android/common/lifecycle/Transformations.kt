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
fun <IN1, IN2, OUT> LiveData<IN1>.switchCombineWith(other: LiveData<IN2>, mapFunction: (IN1?, IN2?) -> LiveData<OUT>?) =
    switchCombineWithInt(this, other) { mapFunction(value, other.value) }

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
    mapFunction: (IN1?, IN2?, IN3?) -> LiveData<OUT>?
) = switchCombineWithInt(this, other, other2) { mapFunction(value, other.value, other2.value) }

/**
 * This method will combine 4 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of the source LiveData objects.
 * Returning any of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
@JvmName("switchCombine")
fun <P, P1, P2, P3, R> LiveData<P>.switchCombineWith(
    other: LiveData<P1>,
    other2: LiveData<P2>,
    other3: LiveData<P3>,
    mapFunction: (P?, P1?, P2?, P3?) -> LiveData<R>?
) = switchCombineWithInt(this, other, other2, other3) { mapFunction(value, other.value, other2.value, other3.value) }

private inline fun <OUT> switchCombineWithInt(
    vararg input: LiveData<*>,
    crossinline mapFunction: () -> LiveData<OUT>?
): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = object : Observer<Any?> {
        private var source: LiveData<OUT> = emptyLiveData()
        override fun onChanged(t: Any?) {
            val newSource = mapFunction()
            if (source == newSource) return
            result.removeSource(source)
            source = newSource.orEmpty()
            result.addSource(source) { value: OUT? -> result.value = value }
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
fun <IN1, IN2, OUT> LiveData<IN1>.combineWith(other: LiveData<IN2>, mapFunction: (IN1?, IN2?) -> OUT?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val observer = Observer<Any?> { result.value = mapFunction(value, other.value) }
    result.addSource(this, observer)
    result.addSource(other, observer)
    return result
}

fun <T> LiveData<out Iterable<T>>.sortedWith(comparator: Comparator<in T>) = map { it?.sortedWith(comparator) }

// Provide Kotlin extensions for existing transformations
// TODO: these can be deprecated after Lifecycle 2.1.0 is released

inline fun <IN, OUT> LiveData<IN>.map(crossinline transform: (IN?) -> OUT?): LiveData<OUT> =
    Transformations.map(this) { transform(it) }

inline fun <X, Y> LiveData<X>.flatMap(crossinline transform: (X) -> LiveData<Y>?): LiveData<Y> =
    Transformations.switchMap(this) { transform(it).orEmpty() }
