package com.google.android.material.tabs

import android.graphics.drawable.Drawable
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val baseBackgroundDrawableField by lazy { getDeclaredFieldOrNull<TabLayout.TabView>("baseBackgroundDrawable") }
internal var TabLayout.TabView.baseBackgroundDrawableCompat: Drawable?
    get() = baseBackgroundDrawableField?.get(this) as? Drawable
    set(value) {
        baseBackgroundDrawableField?.set(this, value)
    }
