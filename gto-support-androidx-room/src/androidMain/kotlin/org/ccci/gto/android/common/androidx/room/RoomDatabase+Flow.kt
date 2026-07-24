package org.ccci.gto.android.common.androidx.room

import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map

fun RoomDatabase.changeFlow(vararg tableName: String): Flow<Unit> =
    invalidationTracker.createFlow(*tableName, emitInitialState = true).map { }.conflate()
