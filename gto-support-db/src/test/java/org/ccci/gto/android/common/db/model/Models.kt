package org.ccci.gto.android.common.db.model

import org.ccci.gto.android.common.db.Table

class Model1 {
    companion object {
        const val TABLE_NAME = "model1"
        const val FIELD_NAME = "field"

        val TABLE = Table.forClass(Model1::class.java)
        val FIELD = TABLE.field(FIELD_NAME)
    }
}
class Model2
