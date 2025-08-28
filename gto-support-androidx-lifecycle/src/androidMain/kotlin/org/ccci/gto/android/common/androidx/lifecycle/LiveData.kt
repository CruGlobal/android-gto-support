package org.ccci.gto.android.common.androidx.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.copyInto(owner: LifecycleOwner, target: MutableLiveData<in T>) =
    observe(owner) { target.value = it }

// region Multi-observe
fun <IN1, IN2> LifecycleOwner.observe(source1: LiveData<IN1>, source2: LiveData<IN2>, observer: (IN1, IN2) -> Unit) =
    compoundLiveData(source1, source2).observe(this) {
        @Suppress("UNCHECKED_CAST")
        observer(source1.value as IN1, source2.value as IN2)
    }

fun <IN1, IN2, IN3> LifecycleOwner.observe(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    observer: (IN1, IN2, IN3) -> Unit
) = compoundLiveData(source1, source2, source3).observe(this) {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2, source3.value as IN3)
}

fun <IN1, IN2> observeForever(source1: LiveData<IN1>, source2: LiveData<IN2>, observer: (IN1, IN2) -> Unit) =
    compoundLiveData(source1, source2).observeForever {
        @Suppress("UNCHECKED_CAST")
        observer(source1.value as IN1, source2.value as IN2)
    }

fun <IN1, IN2, IN3> observeForever(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    observer: (IN1, IN2, IN3) -> Unit
) = compoundLiveData(source1, source2, source3).observeForever {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2, source3.value as IN3)
}

internal fun compoundLiveData(vararg input: LiveData<*>): LiveData<Unit> {
    val result = MediatorLiveData<Unit>()
    val state = object {
        val inputInitialized = BooleanArray(input.size) { false }
        var isInitialized = false
            get() = field || inputInitialized.all { it }.also { field = it }
    }
    input.forEachIndexed { i, it ->
        result.addSource(it) {
            state.inputInitialized[i] = true
            if (state.isInitialized) result.value = Unit
        }
    }
    return result
}
// endregion Multi-observe

// region observeOnce
@MainThread
inline fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, crossinline onChanged: (T) -> Unit) =
    observeOnceObserver(onChanged).also { observe(owner, it) }

@MainThread
inline fun <T> LiveData<T>.observeOnce(crossinline onChanged: (T) -> Unit) =
    observeOnceObserver(onChanged).also { observeForever(it) }

@JvmSynthetic
inline fun <T> LiveData<T>.observeOnceObserver(crossinline onChanged: (T) -> Unit) = object : Observer<T> {
    override fun onChanged(value: T) {
        onChanged.invoke(value)
        removeObserver(this)
    }
}
// endregion observeOnce
