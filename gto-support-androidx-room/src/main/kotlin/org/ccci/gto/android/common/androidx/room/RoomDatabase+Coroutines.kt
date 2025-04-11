package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.RoomDatabase

internal const val FIELD_COROUTINE_SCOPE = "GtoCoroutineScope"

@get:SuppressLint("RestrictedApi")
val RoomDatabase.coroutineScope get() = getCoroutineScope()
