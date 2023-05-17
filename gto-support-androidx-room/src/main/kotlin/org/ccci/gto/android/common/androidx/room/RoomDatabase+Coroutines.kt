package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.room.getQueryDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

internal const val FIELD_COROUTINE_SCOPE = "GtoCoroutineScope"

val RoomDatabase.coroutineScope
    @SuppressLint("RestrictedApi")
    get() = backingFieldMap.getOrPut(FIELD_COROUTINE_SCOPE) {
        CoroutineScope(getQueryDispatcher() + SupervisorJob())
    } as CoroutineScope
