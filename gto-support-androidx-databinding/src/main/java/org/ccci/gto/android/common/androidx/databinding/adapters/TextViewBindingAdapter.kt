package org.ccci.gto.android.common.androidx.databinding.adapters

import android.os.Build
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("textCursorDrawableTint")
internal fun TextView.bindTextCursorDrawableTint(tintColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) textCursorDrawable?.setTint(tintColor)
}
