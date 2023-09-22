package org.ccci.gto.android.common.jsonapi.util

import java.util.TreeSet

class Includes private constructor(include: Collection<String>?, private val base: String = "") {
    constructor(vararg include: String) : this(include.toList())
    constructor(include: Collection<String>?) : this(include, "")
    private constructor(base: Includes, descendant: String) : this(base.include, base.base + descendant + ".")

    private val include = include?.let { TreeSet(it) }

    fun include(relationship: String) = isIncludeAll || isDirectInclude(relationship) || isImplicitInclude(relationship)

    private val isIncludeAll get() = include == null
    private fun isDirectInclude(relationship: String) = include?.contains("$base$relationship") == true
    private fun isImplicitInclude(relationship: String): Boolean {
        val prefix = "$base$relationship."
        return include?.ceiling(prefix)?.startsWith(prefix) == true
    }

    fun descendant(relationship: String) = when (include) {
        null -> this
        else -> Includes(this, relationship)
    }

    val queryParameterValue get() = include.joinToString(",")

    internal companion object {
        /**
         * Merge two Includes objects together. This should only be called on includes with the same base.
         */
        @JvmStatic
        @JvmName("merge")
        internal fun merge(includes: Includes, includes2: Includes): Includes {
            require(includes.base == includes2.base) { "Cannot merge Includes objects with different bases" }
            return Includes(includes.include.orEmpty() + includes2.include.orEmpty())
        }
    }
}
