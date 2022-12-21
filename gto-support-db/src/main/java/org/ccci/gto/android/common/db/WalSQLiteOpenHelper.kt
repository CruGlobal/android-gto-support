package org.ccci.gto.android.common.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

abstract class WalSQLiteOpenHelper @JvmOverloads constructor(
    context: Context?,
    name: String?,
    factory: CursorFactory?,
    version: Int,
    errorHandler: DatabaseErrorHandler? = null,
) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {
    init {
        setWriteAheadLoggingEnabled(true)
    }

    final override fun setWriteAheadLoggingEnabled(enabled: Boolean) = super.setWriteAheadLoggingEnabled(enabled)
}
