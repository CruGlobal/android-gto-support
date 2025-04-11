package org.ccci.gto.android.common.androidx.room

import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

object TestRoomDatabaseCoroutines {
    @Deprecated(
        "Since v4.4.0, define a custom CoroutineContext when creating the RoomDatabase instead.",
        level = DeprecationLevel.ERROR
    )
    fun setCoroutineScope(db: RoomDatabase, scope: CoroutineScope): Nothing = error("This is no longer supported")
}
