package org.ccci.gto.android.common.db

import android.util.Pair

internal class QueryComponent(val sql: String, vararg val args: String) {
    operator fun plus(other: QueryComponent?) = when (other) {
        null -> this
        else -> QueryComponent(sql + other.sql, *args, *other.args)
    }

    fun prepend(sql: String) = QueryComponent(sql + this.sql, *args)
}

internal operator fun QueryComponent?.plus(other: QueryComponent) = when {
    this == null -> other
    else -> QueryComponent(sql + other.sql, *args, *other.args)
}

internal operator fun QueryComponent?.plus(sql: String) = when {
    this == null -> QueryComponent(sql)
    else -> QueryComponent(this.sql + sql, *args)
}

@Deprecated("This is only present to aid conversion from Java to Kotlin")
internal fun Pair<String, Array<String>>.toQueryComponent() = QueryComponent(first, *second)
@Deprecated("This is only present to aid conversion from Java to Kotlin")
internal fun QueryComponent.toPair() = Pair(sql, args.toList().toTypedArray())
