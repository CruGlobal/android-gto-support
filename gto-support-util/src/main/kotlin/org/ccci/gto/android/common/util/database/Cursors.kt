@file:JvmName("CursorUtils")

package org.ccci.gto.android.common.util.database

import android.database.Cursor
import java.util.Locale
import org.ccci.gto.android.common.util.toLocale
import org.jetbrains.annotations.Contract

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getDouble(columnName: String, defValue: Double? = null) = getString(columnName)?.toDoubleOrNull() ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmName("getNonNullDouble")
fun Cursor.getDouble(columnName: String, defValue: Double) = getDouble(columnName) ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getInt(columnName: String, defValue: Int? = null) = getString(columnName)?.toIntOrNull() ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmName("getNonNullInt")
fun Cursor.getInt(columnName: String, defValue: Int) = getInt(columnName) ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return The value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getLong(columnName: String, defValue: Long? = null) = getString(columnName)?.toLongOrNull() ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return The value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmName("getNonNullLong")
fun Cursor.getLong(columnName: String, defValue: Long) = getLong(columnName) ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getString(columnName: String, defValue: String? = null) =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { getString(it) } ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist or is null
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is null or non-existent
 */
@JvmName("getNonNullString")
fun Cursor.getString(columnName: String, defValue: String) = getString(columnName) ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return The value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmOverloads
@Contract("_, _, !null -> !null")
fun Cursor.getLocale(columnName: String, defValue: Locale? = null) = getString(columnName)?.toLocale() ?: defValue

/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @param[defValue] The default value to return when the column doesn't exist, is invalid, or is null
 * @return The value for the specified column in the current row of the specified Cursor.
 * Or the default value if the column is invalid, null or non-existent
 */
@JvmName("getNonNullLocale")
fun Cursor.getLocale(columnName: String, defValue: Locale) = getLocale(columnName) ?: defValue

inline fun <R> Cursor.map(transform: (Cursor) -> R): List<R> = mapTo(ArrayList(count), transform)

inline fun <R, C : MutableCollection<in R>> Cursor.mapTo(destination: C, transform: (Cursor) -> R): C {
    moveToPosition(-1)
    while (moveToNext()) destination.add(transform(this))
    return destination
}
