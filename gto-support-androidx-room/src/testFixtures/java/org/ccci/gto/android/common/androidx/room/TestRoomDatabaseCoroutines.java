package org.ccci.gto.android.common.androidx.room;

import android.annotation.SuppressLint;

import androidx.room.RoomDatabase;

import kotlinx.coroutines.CoroutineScope;

public class TestRoomDatabaseCoroutines {
    @SuppressLint("RestrictedApi")
    public static void setCoroutineScope(RoomDatabase db, CoroutineScope scope) {
        db.getBackingFieldMap().put(RoomDatabase_CoroutinesKt.FIELD_COROUTINE_SCOPE, scope);
    }
}
