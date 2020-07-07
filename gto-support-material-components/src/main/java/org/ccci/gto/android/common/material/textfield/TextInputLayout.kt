package org.ccci.gto.android.common.material.textfield

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.collapsingTextHelperCompat
import com.google.android.material.textfield.editTextCompat
import com.google.android.material.textfield.focusedTextColorCompat
import com.google.android.material.textfield.updateInputLayoutMarginsCompat
import com.google.android.material.textfield.updateLabelStateCompat

@SuppressLint("RestrictedApi")
fun TextInputLayout.setFocusedTextColor(colors: ColorStateList?) {
    collapsingTextHelperCompat.collapsedTextColor = colors
    focusedTextColorCompat = colors
    if (editTextCompat != null) {
        updateLabelStateCompat(false)
        updateInputLayoutMarginsCompat()
    }
}

fun TextInputLayout.setFocusedTextColor(@ColorInt color: Int) = setFocusedTextColor(ColorStateList.valueOf(color))
