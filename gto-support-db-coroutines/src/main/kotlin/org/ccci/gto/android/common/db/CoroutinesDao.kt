package org.ccci.gto.android.common.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

private const val COROUTINES_PARALLELISM = 4

interface CoroutinesDao : Dao {
    @OptIn(ExperimentalCoroutinesApi::class)
    val coroutineDispatcher: CoroutineDispatcher
        get() = getService { Dispatchers.IO.limitedParallelism(COROUTINES_PARALLELISM) }
    val coroutinesScope: CoroutineScope
        get() = getService { CoroutineScope(coroutineDispatcher + SupervisorJob()) }
}
