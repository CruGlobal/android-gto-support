package org.ccci.gto.android.common.db

class QueryComponent internal constructor(val sql: String = "", vararg val args: String) {
    operator fun plus(other: QueryComponent?) = when (other) {
        null -> this
        else -> QueryComponent(sql + other.sql, *args, *other.args)
    }

    fun prepend(sql: String) = QueryComponent(sql + this.sql, *args)
}

operator fun QueryComponent?.plus(other: QueryComponent) = when {
    this == null -> other
    else -> QueryComponent(sql + other.sql, *args, *other.args)
}

operator fun QueryComponent?.plus(sql: String) = when {
    this == null -> QueryComponent(sql)
    else -> QueryComponent(this.sql + sql, *args)
}

internal fun <T> Array<T>.joinToQueryComponent(
    separator: String? = null,
    transform: ((T) -> QueryComponent)
): QueryComponent {
    var output = QueryComponent()
    forEachIndexed { i, it ->
        if (i > 0 && separator != null) output += separator
        output += transform(it)
    }
    return output
}

internal fun <T> Iterable<T>.joinToQueryComponent(
    separator: String? = null,
    transform: ((T) -> QueryComponent)
): QueryComponent {
    var output = QueryComponent()
    forEachIndexed { i, it ->
        if (i > 0 && separator != null) output += separator
        output += transform(it)
    }
    return output
}
