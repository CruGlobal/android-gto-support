package org.ccci.gto.android.common.material.tabs

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.baseBackgroundDrawableCompat
import com.google.android.material.tabs.populateFromPagerAdapterCompat

fun TabLayout.notifyPagerAdapterChanged() = populateFromPagerAdapterCompat()

var TabLayout.Tab.background: Drawable?
    get() = view.baseBackgroundDrawableCompat
    set(value) {
        view.baseBackgroundDrawableCompat = value
        view.invalidate()
        parent?.invalidate()
    }

fun TabLayout.Tab.setBackgroundTint(@ColorInt tint: Int) {
    background = background?.let { DrawableCompat.wrap(it).mutate() }
        ?.apply { DrawableCompat.setTint(this, tint) }
}

var TabLayout.Tab.visibility: Int
    get() = view.visibility
    set(value) {
        view.visibility = value
    }
