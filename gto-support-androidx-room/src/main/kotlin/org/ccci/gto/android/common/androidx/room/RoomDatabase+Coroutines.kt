package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.RoomDatabase

@get:SuppressLint("RestrictedApi")
val RoomDatabase.coroutineScope get() = getCoroutineScope()
