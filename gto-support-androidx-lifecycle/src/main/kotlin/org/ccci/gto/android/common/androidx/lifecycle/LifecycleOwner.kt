package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

// region Multi-observe
fun <IN1, IN2> LifecycleOwner.observe(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    observer: (IN1, IN2) -> Unit
) = observeInt(source1, source2) {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2)
}

fun <IN1, IN2, IN3> LifecycleOwner.observe(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    observer: (IN1, IN2, IN3) -> Unit
) = observeInt(source1, source2, source3) {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2, source3.value as IN3)
}

private fun LifecycleOwner.observeInt(
    vararg input: LiveData<*>,
    observer: Observer<Unit>
) {
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
    result.observe(this, observer)
}
// endregion Multi-observe
