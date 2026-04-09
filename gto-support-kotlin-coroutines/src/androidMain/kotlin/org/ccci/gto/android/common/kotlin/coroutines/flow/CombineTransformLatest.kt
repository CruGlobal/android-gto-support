package org.ccci.gto.android.common.kotlin.coroutines.flow

import kotlin.experimental.ExperimentalTypeInference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transformLatest

// Copied from https://github.com/Kotlin/kotlinx.coroutines/issues/1484#issuecomment-699600367
// These methods can go away if there is ever an official combineTransformLatest provided by the coroutines library.

@OptIn(ExperimentalTypeInference::class)
fun <T1, T2, R> combineTransformLatest(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2) -> Unit
): Flow<R> = combineTransformLatest(flow, flow2) { args ->
    @Suppress("UNCHECKED_CAST")
    transform(
        args[0] as T1,
        args[1] as T2
    )
}

@OptIn(ExperimentalTypeInference::class)
fun <T1, T2, T3, R> combineTransformLatest(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2, T3) -> Unit
): Flow<R> = combineTransformLatest(flow, flow2, flow3) { args ->
    @Suppress("UNCHECKED_CAST")
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
    )
}

@JvmSynthetic
@JvmName("flowCombineTransformLatest")
@OptIn(ExperimentalTypeInference::class)
fun <T1, T2, R> Flow<T1>.combineTransformLatest(
    flow2: Flow<T2>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2) -> Unit
): Flow<R> = combineTransformLatest(this, flow2, transform)

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTypeInference::class)
inline fun <reified T, R> combineTransformLatest(
    vararg flows: Flow<T>,
    @BuilderInference noinline transform: suspend FlowCollector<R>.(Array<T>) -> Unit
): Flow<R> = combine(*flows) { it }.transformLatest(transform)
