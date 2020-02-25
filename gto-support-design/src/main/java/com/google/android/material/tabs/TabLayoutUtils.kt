@file:JvmName("TabLayoutUtils")

package com.google.android.material.tabs

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val baseBackgroundDrawableField by lazy { getDeclaredFieldOrNull<TabLayout.TabView>("baseBackgroundDrawable") }
private inline var TabLayout.TabView.baseBackgroundDrawable: Drawable?
    get() = baseBackgroundDrawableField?.get(this) as? Drawable
    set(value) {
        baseBackgroundDrawableField?.set(this, value)
    }

var TabLayout.Tab.background: Drawable?
    get() = view.baseBackgroundDrawable
    set(value) {
        view.baseBackgroundDrawable = value
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
