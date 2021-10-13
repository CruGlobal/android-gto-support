@file:BindingMethods(
    BindingMethod(
        type = TextInputLayout::class,
        attribute = ANDROID_TEXT_COLOR_HINT,
        method = "setDefaultHintTextColor"
    )
)

package org.ccci.gto.android.common.material.textfield.databinding

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.google.android.material.textfield.TextInputLayout

private const val ANDROID_TEXT_COLOR_HINT = "android:textColorHint"
private const val HINT_TEXT_COLOR = "hintTextColor"

@BindingAdapter(ANDROID_TEXT_COLOR_HINT, HINT_TEXT_COLOR)
internal fun TextInputLayout.bindHintTextColors(defaultHintTextColor: ColorStateList?, hintTextColor: ColorStateList?) {
    setDefaultHintTextColor(defaultHintTextColor)
    setHintTextColor(hintTextColor)
}
