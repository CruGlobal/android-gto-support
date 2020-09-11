package org.ccci.gto.android.common.util.database

import android.database.Cursor
import org.json.JSONException
import org.json.JSONObject

// region JSONObject
/**
 * @receiver The Cursor we are fetching the value from
 * @param[columnName] The column we are requesting the value of
 * @return the value for the specified column in the current row of the specified Cursor.
 * Or null if the column is non-existent or an invalid value
 */
fun Cursor.getJSONObjectOrNull(field: String) = try {
    getString(field)?.let { JSONObject(it) }
} catch (ignored: JSONException) {
    null
}
// endregion JSONObject
