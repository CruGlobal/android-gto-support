package org.ccci.gto.android.common.jsonapi.retrofit2

import androidx.annotation.VisibleForTesting

@VisibleForTesting
internal const val PARAM_INCLUDE = "include"
private const val PARAM_FIELDS = "fields"
private const val PARAM_SORT = "sort"

abstract class BaseJsonApiParams<T : BaseJsonApiParams<T>> : MutableMap<String, String> by mutableMapOf() {
    protected abstract val self: T

    fun addAll(vararg params: Map<String, String>): T {
        params.forEach { putAll(it) }
        return self
    }

    fun include(vararg paths: String): T {
        this[PARAM_INCLUDE] = listOf(this[PARAM_INCLUDE], *paths).filterNot { it.isNullOrEmpty() }.joinToString(",")
        return self
    }

    fun clearIncludes(): T {
        remove(PARAM_INCLUDE)
        return self
    }

    fun fields(type: String, vararg fields: String): T {
        val param = fieldsFor(type)
        this[param] = listOf(this[param], *fields).filterNot { it.isNullOrEmpty() }.joinToString(",")
        return self
    }

    fun clearFields(type: String): T {
        remove(fieldsFor(type))
        return self
    }

    fun sort(vararg keys: String): T {
        put(PARAM_SORT, keys.joinToString(","))
        return self
    }
}

private fun fieldsFor(type: String) = "$PARAM_FIELDS[$type]"
