package org.ccci.gto.android.common.db

import android.content.ContentValues
import android.database.Cursor
import org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_DATA1
import org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_DATA2
import org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_ID1
import org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_ID2
import org.ccci.gto.android.common.db.model.Compound
import org.ccci.gto.android.common.util.database.getString

internal object CompoundMapper : AbstractMapper<Compound>() {
    override fun mapField(values: ContentValues, field: String, obj: Compound) {
        when (field) {
            COLUMN_ID1 -> values.put(field, obj.id1)
            COLUMN_ID2 -> values.put(field, obj.id2)
            COLUMN_DATA1 -> values.put(field, obj.data1)
            COLUMN_DATA2 -> values.put(field, obj.data2)
            else -> super.mapField(values, field, obj)
        }
    }

    override fun newObject(c: Cursor) = Compound(
        c.getString(COLUMN_ID1, ""),
        c.getString(COLUMN_ID2, "")
    )

    override fun toObject(c: Cursor) = super.toObject(c).apply {
        data1 = c.getString(COLUMN_DATA1)
        data2 = c.getString(COLUMN_DATA2)
    }
}
