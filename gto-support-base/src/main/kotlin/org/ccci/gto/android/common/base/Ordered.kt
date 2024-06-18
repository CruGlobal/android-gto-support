package org.ccci.gto.android.common.base

interface Ordered {
    val order: Int get() = DEFAULT_PRECEDENCE

    companion object {
        const val HIGHEST_PRECEDENCE = Int.MIN_VALUE
        const val DEFAULT_PRECEDENCE = 0
        const val LOWEST_PRECEDENCE = Int.MAX_VALUE

        val COMPARATOR = compareBy<Any?> { (it as? Ordered)?.order ?: DEFAULT_PRECEDENCE }
    }
}
