package org.ccci.gto.android.common.jsonapi.util

import java.util.TreeSet

class Includes private constructor(
    private val include: TreeSet<String> = TreeSet(),
    private val base: String = "",
) {
    constructor(vararg include: String) : this(TreeSet(include.toList()))
    constructor(include: Collection<String>) : this(TreeSet(include))
    private constructor(base: Includes, descendant: String) : this(base.include, "${base.base}$descendant.")

    fun include(relationship: String) = isDirectInclude(relationship) || isImplicitInclude(relationship)

    private fun isDirectInclude(relationship: String) = "$base$relationship" in include
    private fun isImplicitInclude(relationship: String): Boolean {
        val prefix = "$base$relationship."
        return include.ceiling(prefix)?.startsWith(prefix) == true
    }

    fun descendant(relationship: String) = Includes(this, relationship)

    val queryParameterValue get() = include.joinToString(",")

    internal companion object {
        /**
         * Merge two Includes objects together. This should only be called on includes with the same base.
         */
        @JvmStatic
        @JvmName("merge")
        internal fun merge(includes: Includes, includes2: Includes): Includes {
            require(includes.base == includes2.base) { "Cannot merge Includes objects with different bases" }
            return Includes(includes.include + includes2.include)
        }
    }
}
