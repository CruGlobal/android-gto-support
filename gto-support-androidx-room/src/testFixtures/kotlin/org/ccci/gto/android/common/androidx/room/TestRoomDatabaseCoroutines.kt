package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

object TestRoomDatabaseCoroutines {
    @SuppressLint("RestrictedApi")
    fun setCoroutineScope(db: RoomDatabase, scope: CoroutineScope) {
        db.backingFieldMap[FIELD_COROUTINE_SCOPE] = scope
    }

    @SuppressLint("RestrictedApi")
    fun resetCoroutineScope(db: RoomDatabase) {
        db.backingFieldMap.remove(FIELD_COROUTINE_SCOPE)
    }
}
