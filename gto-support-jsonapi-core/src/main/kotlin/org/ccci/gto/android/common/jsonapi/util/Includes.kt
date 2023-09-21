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

    /**
     * Merge two base Includes objects together. This should only ever be called on a base includes object.
     */
    fun merge(includes: Includes?): Includes {
        // throw an error if this is a descendant includes object
        check("" == base) { "Cannot merge includes with a descendant Includes object" }

        // short-circuit if we aren't actually merging an Includes object
        if (includes == null) return this

        // throw an error if the includes object being merged is a descendant includes object
        require("" == includes.base) { "Cannot merge a descendant Includes object" }

        // merge rules: include all overrides everything, otherwise merge the includes
        return when {
            isIncludeAll -> this
            includes.isIncludeAll -> includes
            else -> Includes(include.orEmpty() + includes.include.orEmpty())
        }
    }

    fun descendant(relationship: String) = when (include) {
        null -> this
        else -> Includes(this, relationship)
    }

    val queryParameterValue get() = include.joinToString(",")
}
