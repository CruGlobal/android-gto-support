package org.ccci.gto.android.common

import org.ccci.gto.android.common.base.Ordered as OrderedBase

@Deprecated("Since v4.2.2, use Ordered from gto-support-base instead.")
interface Ordered : OrderedBase {
    companion object {
        @Deprecated("Since v4.2.2, use Ordered.HIGHEST_PRECEDENCE from gto-support-base instead.")
        const val HIGHEST_PRECEDENCE = OrderedBase.HIGHEST_PRECEDENCE
        @Deprecated("Since v4.2.2, use Ordered.DEFAULT_PRECEDENCE from gto-support-base instead.")
        const val DEFAULT_PRECEDENCE = OrderedBase.DEFAULT_PRECEDENCE
        @Deprecated("Since v4.2.2, use Ordered.LOWEST_PRECEDENCE from gto-support-base instead.")
        const val LOWEST_PRECEDENCE = OrderedBase.LOWEST_PRECEDENCE

        @Deprecated(
            "Since v4.2.2, use Ordered.COMPARATOR from gto-support-base instead.",
            ReplaceWith("Ordered.COMPARATOR", "org.ccci.gto.android.common.base.Ordered"),
        )
        val COMPARATOR get() = OrderedBase.COMPARATOR
    }
}
