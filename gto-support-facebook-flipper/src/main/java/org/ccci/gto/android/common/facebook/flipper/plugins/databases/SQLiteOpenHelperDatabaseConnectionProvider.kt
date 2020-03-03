package org.ccci.gto.android.common.facebook.flipper.plugins.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseConnectionProvider
import java.io.File

class SQLiteOpenHelperDatabaseConnectionProvider @JvmOverloads constructor(
    context: Context,
    private val fallback: SqliteDatabaseConnectionProvider = DefaultSqliteDatabaseConnectionProvider,
    vararg dbs: SQLiteOpenHelper
) : SqliteDatabaseConnectionProvider {
    private val dbs = dbs.associateBy { context.getDatabasePath(it.databaseName) }

    override fun openDatabase(databaseFile: File): SQLiteDatabase =
        dbs[databaseFile]?.writableDatabase?.apply { acquireReference() } ?: fallback.openDatabase(databaseFile)
}
