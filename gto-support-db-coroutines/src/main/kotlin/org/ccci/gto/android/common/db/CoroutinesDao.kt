package org.ccci.gto.android.common.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val COROUTINES_PARALLELISM = 4

interface CoroutinesDao : Dao

@OptIn(ExperimentalCoroutinesApi::class)
internal val CoroutinesDao.dispatcher: CoroutineDispatcher
    get() = getService { Dispatchers.IO.limitedParallelism(COROUTINES_PARALLELISM) }
