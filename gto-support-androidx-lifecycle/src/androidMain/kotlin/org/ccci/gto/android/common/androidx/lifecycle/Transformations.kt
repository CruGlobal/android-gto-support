@file:JvmName("Transformations2")

package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap

// region combine
/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
fun <IN1, IN2, OUT> combine(source1: LiveData<IN1>, source2: LiveData<IN2>, mapFunction: (IN1, IN2) -> OUT) =
    combineInt(source1, source2) {
        @Suppress("UNCHECKED_CAST")
        mapFunction(source1.value as IN1, source2.value as IN2)
    }

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmSynthetic
fun <IN1, IN2, OUT> LiveData<IN1>.combineWith(other: LiveData<IN2>, mapFunction: (IN1, IN2) -> OUT) =
    combineInt(this, other) {
        @Suppress("UNCHECKED_CAST")
        mapFunction(value as IN1, other.value as IN2)
    }

/**
 * This method will combine 3 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
fun <IN1, IN2, IN3, OUT> combine(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    mapFunction: (IN1, IN2, IN3) -> OUT,
) = combineInt(source1, source2, source3) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(source1.value as IN1, source2.value as IN2, source3.value as IN3)
}

/**
 * This method will combine 3 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmSynthetic
fun <IN1, IN2, IN3, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    mapFunction: (IN1, IN2, IN3) -> OUT,
) = combineInt(this, other, other2) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2, other2.value as IN3)
}

/**
 * This method will combine 4 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
fun <IN1, IN2, IN3, IN4, OUT> combine(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    source4: LiveData<IN4>,
    mapFunction: (IN1, IN2, IN3, IN4) -> OUT,
) = combineInt(source1, source2, source3, source4) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(source1.value as IN1, source2.value as IN2, source3.value as IN3, source4.value as IN4)
}

/**
 * This method will combine 4 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmSynthetic
fun <IN1, IN2, IN3, IN4, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    mapFunction: (IN1, IN2, IN3, IN4) -> OUT,
) = combineInt(this, other, other2, other3) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2, other2.value as IN3, other3.value as IN4)
}

/**
 * This method will combine 5 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
fun <IN1, IN2, IN3, IN4, IN5, OUT> combine(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    source4: LiveData<IN4>,
    source5: LiveData<IN5>,
    mapFunction: (IN1, IN2, IN3, IN4, IN5) -> OUT,
) = combineInt(source1, source2, source3, source4, source5) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(
        source1.value as IN1,
        source2.value as IN2,
        source3.value as IN3,
        source4.value as IN4,
        source5.value as IN5
    )
}

/**
 * This method will combine 5 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmSynthetic
fun <IN1, IN2, IN3, IN4, IN5, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    other4: LiveData<IN5>,
    mapFunction: (IN1, IN2, IN3, IN4, IN5) -> OUT,
) = combineInt(this, other, other2, other3, other4) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2, other2.value as IN3, other3.value as IN4, other4.value as IN5)
}

/**
 * This method will combine 6 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmName("combine")
fun <IN1, IN2, IN3, IN4, IN5, IN6, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    other4: LiveData<IN5>,
    other5: LiveData<IN6>,
    mapFunction: (IN1, IN2, IN3, IN4, IN5, IN6) -> OUT,
) = combineInt(this, other, other2, other3, other4, other5) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(
        value as IN1,
        other.value as IN2,
        other2.value as IN3,
        other3.value as IN4,
        other4.value as IN5,
        other5.value as IN6
    )
}

/**
 * This method will combine 7 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of all source LiveData objects.
 *
 * @see androidx.lifecycle.Transformations.map
 */
@JvmName("combine")
fun <IN1, IN2, IN3, IN4, IN5, IN6, IN7, OUT> LiveData<IN1>.combineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    other4: LiveData<IN5>,
    other5: LiveData<IN6>,
    other6: LiveData<IN7>,
    mapFunction: (IN1, IN2, IN3, IN4, IN5, IN6, IN7) -> OUT,
) = combineInt(this, other, other2, other3, other4, other5, other6) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(
        value as IN1,
        other.value as IN2,
        other2.value as IN3,
        other3.value as IN4,
        other4.value as IN5,
        other5.value as IN6,
        other6.value as IN7
    )
}

private inline fun <OUT> combineInt(vararg input: LiveData<*>, crossinline mapFunction: () -> OUT): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val state = object {
        val inputInitialized = BooleanArray(input.size) { false }
        var isInitialized = false
            get() = field || inputInitialized.all { it }.also { field = it }
    }
    input.forEachIndexed { i, it ->
        result.addSource(it) {
            state.inputInitialized[i] = true
            if (state.isInitialized) result.value = mapFunction()
        }
    }
    return result
}
// endregion combine

inline fun <reified T> LiveData<*>.filterIsInstance() = map { it as? T }

fun <T : Any> LiveData<T?>.notNull(): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { it?.let { result.value = it } }
    return result
}

fun <T> LiveData<out Iterable<T>>.sortedWith(comparator: Comparator<in T>) = map { it.sortedWith(comparator) }

// region switchCombineWith

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 * Returning either of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
fun <IN1, IN2, OUT> switchCombine(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    mapFunction: (IN1, IN2) -> LiveData<out OUT>,
) = switchCombineWithInt(source1, source2) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(source1.value as IN1, source2.value as IN2)
}

/**
 * This method will combine 2 LiveData objects into a new LiveData object by running the {@param mapFunction} on the
 * current values of both source LiveData objects.
 * Returning either of the original LiveData objects will cause an Exception.
 *
 * @see androidx.lifecycle.Transformations.switchMap
 */
fun <IN1, IN2, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    mapFunction: (IN1, IN2) -> LiveData<out OUT>,
) = switchCombineWithInt(this, other) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2)
}

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
    mapFunction: (IN1, IN2, IN3) -> LiveData<out OUT>,
) = switchCombineWithInt(this, other, other2) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2, other2.value as IN3)
}

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
    mapFunction: (IN1, IN2, IN3, IN4) -> LiveData<out OUT>,
) = switchCombineWithInt(this, other, other2, other3) {
    @Suppress("UNCHECKED_CAST")
    mapFunction(value as IN1, other.value as IN2, other2.value as IN3, other3.value as IN4)
}

private inline fun <OUT> switchCombineWithInt(
    vararg input: LiveData<*>,
    crossinline mapFunction: () -> LiveData<out OUT>,
): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    val state = object {
        val inputInitialized = BooleanArray(input.size) { false }
        var isInitialized = false
            get() = field || inputInitialized.all { it }.also { field = it }
        var source: LiveData<out OUT>? = null
    }
    input.forEachIndexed { i, it ->
        result.addSource(it) {
            with(state) {
                inputInitialized[i] = true
                if (!isInitialized) return@addSource
                val newSource = mapFunction()
                if (source === newSource) return@addSource
                source?.let { result.removeSource(it) }
                source = newSource
                source?.let { result.addSource(it, result::setValue) }
            }
        }
    }
    return result
}
// endregion switchCombineWith

// region Boolean operators
infix fun LiveData<Boolean>.and(other: LiveData<Boolean>) = combineWith(other) { t, o -> t && o }
infix fun LiveData<Boolean>.or(other: LiveData<Boolean>) = combineWith(other) { t, o -> t || o }
// endregion Boolean operators

inline fun <T, R> LiveData<out Iterable<T>>.switchFold(crossinline operation: (acc: LiveData<R?>, T) -> LiveData<R?>) =
    switchFold(emptyLiveData(), operation)

inline fun <T, R> LiveData<out Iterable<T>>.switchFold(
    acc: LiveData<R>,
    crossinline operation: (acc: LiveData<R>, T) -> LiveData<R>,
) = switchMap { it.fold(acc, operation) }

/**
 * Transform a LiveData to return an initial value before it has had a chance to resolve it's actual value.
 * This shouldn't be used with [MutableLiveData] which already has a mechanism to define an initial value.
 */
fun <T> LiveData<T>.withInitialValue(value: T): LiveData<T> {
    // short-circuit if the LiveData has already loaded an initial value
    if (isInitialized) return this

    return MediatorLiveData<T>().also {
        it.value = value
        it.addSource(this) { value -> it.value = value }
    }
}
