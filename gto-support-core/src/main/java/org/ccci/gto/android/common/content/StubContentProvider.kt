package org.ccci.gto.android.common.content

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri

class StubContentProvider : ContentProvider() {
    override fun onCreate() = true
    override fun getType(uri: Uri) = null
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ) = null

    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?) = 0
}
