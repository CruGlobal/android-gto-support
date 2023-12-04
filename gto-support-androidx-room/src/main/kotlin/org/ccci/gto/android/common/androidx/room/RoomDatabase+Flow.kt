package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.CoroutinesRoom.Companion.createFlow
import androidx.room.RoomDatabase

@SuppressLint("RestrictedApi")
fun RoomDatabase.changeFlow(vararg tableName: String) = createFlow(
    this,
    inTransaction = false,
    tableNames = arrayOf(*tableName),
    callable = {}
)
