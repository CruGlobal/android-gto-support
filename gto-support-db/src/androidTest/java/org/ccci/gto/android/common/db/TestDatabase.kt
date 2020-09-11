package org.ccci.gto.android.common.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.ccci.gto.android.common.db.CommonTables.LastSyncTable
import org.ccci.gto.android.common.db.Contract.CompoundTable
import org.ccci.gto.android.common.db.Contract.RootTable

internal class TestDatabase private constructor(context: Context) : WalSQLiteOpenHelper(context, "test_db", null, 1) {
    init {
        resetDatabase(writableDatabase)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.apply {
            execSQL(RootTable.SQL_CREATE_TABLE)
            execSQL(CompoundTable.SQL_CREATE_TABLE)
            execSQL(LastSyncTable.SQL_CREATE_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
        error("onUpgrade should not be triggered")

    private fun resetDatabase(db: SQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL(RootTable.SQL_DELETE_TABLE)
            db.execSQL(CompoundTable.SQL_DELETE_TABLE)
            db.execSQL(LastSyncTable.SQL_DELETE_TABLE)
            onCreate(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private var instance: TestDatabase? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context) = instance ?: TestDatabase(context.applicationContext).also { instance = it }
    }
}
