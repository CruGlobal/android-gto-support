package org.ccci.gto.android.common.db

import androidx.annotation.RestrictTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

private const val COROUTINES_PARALLELISM = 4

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
interface CoroutinesDao : Dao {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    val coroutineDispatcher: CoroutineDispatcher
        get() = getService { Dispatchers.IO.limitedParallelism(COROUTINES_PARALLELISM) }
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    val coroutineScope: CoroutineScope
        get() = getService { CoroutineScope(coroutineDispatcher + SupervisorJob()) }
}
