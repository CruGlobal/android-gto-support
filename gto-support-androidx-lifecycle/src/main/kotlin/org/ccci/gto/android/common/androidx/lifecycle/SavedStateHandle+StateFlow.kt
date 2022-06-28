package org.ccci.gto.android.common.androidx.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

@MainThread
@Deprecated(
    "Since v3.12.0, use getMutableStateFlow() or the official getStateFlow in Lifecycle 2.5.0",
    ReplaceWith("getMutableStateFlow(scope, key, initialValue)")
)
fun <T> SavedStateHandle.getStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> = getMutableStateFlow(scope, key, initialValue)

@MainThread
fun <T> SavedStateHandle.getMutableStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> {
    val liveData = getLiveData(key, initialValue)
    val stateFlow = MutableStateFlow(initialValue)

    val observer = Observer<T> { if (it != stateFlow.value) stateFlow.value = it }
    liveData.observeForever(observer)

    stateFlow
        .onCompletion { liveData.removeObserver(observer) }
        .onEach { if (liveData.value != it) liveData.value = it }
        .launchIn(scope + Dispatchers.Main.immediate)

    return stateFlow
}
