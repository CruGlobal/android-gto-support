package org.ccci.gto.android.common.facebook.flipper.plugins.databases

import android.database.sqlite.SQLiteDatabase
import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseConnectionProvider
import java.io.File

class DefaultSqliteDatabaseConnectionProvider : SqliteDatabaseConnectionProvider {
    override fun openDatabase(databaseFile: File) =
        SQLiteDatabase.openDatabase(databaseFile.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
}
