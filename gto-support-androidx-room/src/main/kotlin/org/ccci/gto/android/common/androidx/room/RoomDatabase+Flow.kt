package org.ccci.gto.android.common.androidx.room

import androidx.room.CoroutinesRoom.Companion.createFlow
import androidx.room.RoomDatabase

fun RoomDatabase.changeFlow(vararg tableName: String) = createFlow(
    this,
    inTransaction = false,
    tableNames = arrayOf(*tableName),
    callable = {}
)
