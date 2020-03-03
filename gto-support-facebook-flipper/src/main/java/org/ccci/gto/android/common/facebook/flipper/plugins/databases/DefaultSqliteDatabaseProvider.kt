package org.ccci.gto.android.common.facebook.flipper.plugins.databases

import android.content.Context
import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseProvider

class DefaultSqliteDatabaseProvider(private val context: Context) : SqliteDatabaseProvider {
    override fun getDatabaseFiles() = context.databaseList().map { context.getDatabasePath(it) }
}
