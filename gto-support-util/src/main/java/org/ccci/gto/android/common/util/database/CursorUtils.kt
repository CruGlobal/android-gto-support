@file:JvmName("CursorUtils")

package org.ccci.gto.android.common.util.database

import android.database.Cursor
import org.jetbrains.annotations.Contract

/**
 * @receiver The Cursor we are fetching the value from
 * @param columnName The column we are requesting the value of
 * @param defValue The default value to return when the column doesn't exist or is null
 * @return the value for the specified column in the current row of the specified Cursor. Or the default value if the column is null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getString(columnName: String, defValue: String? = null) =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { getString(it) } ?: defValue
