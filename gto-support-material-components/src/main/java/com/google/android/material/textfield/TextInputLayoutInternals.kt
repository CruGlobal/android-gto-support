package com.google.android.material.textfield

import android.content.res.ColorStateList
import android.widget.EditText
import com.google.android.material.internal.CollapsingTextHelper
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull
import org.ccci.gto.android.common.util.getDeclaredMethodOrNull

private val focusedTextColorField by lazy { getDeclaredFieldOrNull<TextInputLayout>("focusedTextColor") }
private val TextInputLayout.updateInputLayoutMarginsMethod by lazy {
    getDeclaredMethodOrNull<TextInputLayout>("updateInputLayoutMargins")
}

internal val TextInputLayout.collapsingTextHelperCompat: CollapsingTextHelper get() = collapsingTextHelper
internal val TextInputLayout.editTextCompat: EditText? get() = editText
internal var TextInputLayout.focusedTextColorCompat: ColorStateList?
    get() = focusedTextColorField?.get(this) as? ColorStateList
    set(value) {
        focusedTextColorField?.set(this, value)
    }

internal fun TextInputLayout.updateInputLayoutMarginsCompat() = updateInputLayoutMarginsMethod?.invoke(this)
internal fun TextInputLayout.updateLabelStateCompat(animate: Boolean) = updateLabelState(animate)
