package org.ccci.gto.android.common.material.textfield

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.textfield.TextInputLayout

@Deprecated(
    "Since v3.6.1, set the hintTextColor on the TextInputLayout instead.",
    ReplaceWith("hintTextColor = colors")
)
fun TextInputLayout.setFocusedTextColor(colors: ColorStateList?) {
    hintTextColor = colors
}

@Deprecated(
    "Since v3.6.1, set the hintTextColor on the TextInputLayout instead.",
    ReplaceWith("hintTextColor = ColorStateList.valueOf(color)", "android.content.res.ColorStateList")
)
fun TextInputLayout.setFocusedTextColor(@ColorInt color: Int) {
    hintTextColor = ColorStateList.valueOf(color)
}
