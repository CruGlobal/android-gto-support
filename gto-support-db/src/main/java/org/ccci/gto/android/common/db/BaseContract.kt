package org.ccci.gto.android.common.db

import android.provider.BaseColumns

abstract class BaseContract {
    interface Base : BaseColumns {
        companion object {
            const val COLUMN_ROWID = BaseColumns._ID
            const val SQL_COLUMN_ROWID = "$COLUMN_ROWID INTEGER PRIMARY KEY"
        }
    }

    companion object {
        @JvmStatic
        fun uniqueIndex(vararg columns: String): String {
            require(columns.isNotEmpty()) { "There needs to be at least 1 column specified for a Unique index" }
            return "UNIQUE(${columns.joinToString(",")})"
        }

        @JvmStatic
        fun create(table: String, vararg sqlColumns: String) = "CREATE TABLE $table (${sqlColumns.joinToString(",")})"

        @JvmStatic
        fun drop(table: String) = "DROP TABLE IF EXISTS $table"
    }
}
