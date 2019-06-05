package org.ccci.gto.android.common.db

import android.content.ContentValues
import android.database.Cursor
import org.ccci.gto.android.common.db.Contract.RootTable.COLUMN_ID
import org.ccci.gto.android.common.db.Contract.RootTable.COLUMN_TEST
import org.ccci.gto.android.common.db.model.Root
import org.ccci.gto.android.common.db.util.CursorUtils
import org.ccci.gto.android.common.util.database.getString

internal class RootMapper : AbstractMapper<Root>() {
    override fun mapField(values: ContentValues, field: String, obj: Root) = when (field) {
        COLUMN_ID -> values.put(field, obj.id)
        COLUMN_TEST -> values.put(field, obj.test)
        else -> super.mapField(values, field, obj)
    }

    override fun newObject(c: Cursor) = Root()
    override fun toObject(c: Cursor) = super.toObject(c).apply {
        id = CursorUtils.getLong(c, COLUMN_ID)
        test = c.getString(COLUMN_TEST)
    }
}
