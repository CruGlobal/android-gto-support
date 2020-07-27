package org.ccci.gto.android.common.androidx.databinding.adapters

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter
import java.util.Locale
import org.ccci.gto.android.common.util.format

@BindingAdapter("number")
fun TextView.bindNumber(amount: Number?) = bindNumber(amount, null)

@SuppressLint("RestrictedApi")
@BindingAdapter("number", "locale")
fun TextView.bindNumber(amount: Number?, locale: Locale?) {
    val value = when {
        locale == null -> amount?.format()
        else -> amount?.format(locale)
    }
    TextViewBindingAdapter.setText(this, value)
}
