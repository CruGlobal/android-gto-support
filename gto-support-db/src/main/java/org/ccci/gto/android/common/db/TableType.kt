package org.ccci.gto.android.common.db

internal class TableType(
    val table: String,
    val projection: Array<String>?,
    val mapper: Mapper<*>?,
    val primaryWhere: Expression?,
)
