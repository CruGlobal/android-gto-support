package org.ccci.gto.android.common.kotlin.coroutines

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.withContext

suspend fun <T> Flow<T>.collectInto(target: MutableLiveData<T>) =
    withContext(Dispatchers.Main.immediate) { conflate().collect { target.value = it } }
