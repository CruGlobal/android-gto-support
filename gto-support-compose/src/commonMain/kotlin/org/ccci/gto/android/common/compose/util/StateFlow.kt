package org.ccci.gto.android.common.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun <T> rememberStateFlow(value: T): StateFlow<T> {
    val mutableStateFlow = remember { MutableStateFlow(value) }
    mutableStateFlow.value = value
    return remember(mutableStateFlow) { mutableStateFlow.asStateFlow() }
}
