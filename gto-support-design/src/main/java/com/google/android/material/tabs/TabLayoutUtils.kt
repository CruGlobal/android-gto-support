@file:JvmName("TabLayoutUtils")

package com.google.android.material.tabs

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import org.ccci.gto.android.common.material.tabs.background
import org.ccci.gto.android.common.material.tabs.notifyPagerAdapterChanged
import org.ccci.gto.android.common.material.tabs.setBackgroundTint

@Deprecated("Since v3.6.1, use extension property from gto-support-material-components instead")
var TabLayout.Tab.background: Drawable?
    get() = background
    set(value) {
        background = value
    }

@Deprecated(
    "Since v3.6.1, use extension method from gto-support-material-components instead",
    ReplaceWith("setBackgroundTint(tint)", "org.ccci.gto.android.common.material.tabs.setBackgroundTint")
)
fun TabLayout.Tab.setBackgroundTint(@ColorInt tint: Int) = setBackgroundTint(tint)

@Deprecated("Since v3.6.1, use extension property from gto-support-material-components instead")
var TabLayout.Tab.visibility: Int
    get() = view.visibility
    set(value) {
        view.visibility = value
    }

@Deprecated(
    "Since v3.6.1, use extension method from gto-support-material-components instead",
    ReplaceWith("notifyPagerAdapterChanged()", "org.ccci.gto.android.common.material.tabs.notifyPagerAdapterChanged")
)
fun TabLayout.notifyPagerAdapterChanged() = notifyPagerAdapterChanged()
