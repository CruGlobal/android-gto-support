@file:BindingMethods(
    BindingMethod(type = TabLayout::class, attribute = ANDROID_TEXT_COLOR, method = "setTabTextColors"),
    BindingMethod(type = TabLayout::class, attribute = TAB_TEXT_COLOR, method = "setTabTextColors")
)

package org.ccci.gto.android.common.material.tabs.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.google.android.material.tabs.TabLayout

private const val ANDROID_TEXT_COLOR = "android:textColor"
private const val TAB_TEXT_COLOR = "tabTextColor"
private const val TAB_SELECTED_TEXT_COLOR = "tabSelectedTextColor"

@BindingAdapter(ANDROID_TEXT_COLOR, TAB_SELECTED_TEXT_COLOR)
internal fun TabLayout.bindAndroidTextColor(normalColor: Int, selectedColor: Int) =
    setTabTextColors(normalColor, selectedColor)

@BindingAdapter(TAB_TEXT_COLOR, TAB_SELECTED_TEXT_COLOR)
internal fun TabLayout.bindTabTextColor(normalColor: Int, selectedColor: Int) =
    setTabTextColors(normalColor, selectedColor)
