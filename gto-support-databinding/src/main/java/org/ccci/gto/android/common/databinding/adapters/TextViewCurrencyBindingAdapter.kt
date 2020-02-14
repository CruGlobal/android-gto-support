package org.ccci.gto.android.common.databinding.adapters

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter
import org.ccci.gto.android.common.util.formatCurrency

@SuppressLint("RestrictedApi")
@BindingAdapter("android:text", "currency")
fun TextView.bindCurrency(amount: Double?, currency: String?) =
    TextViewBindingAdapter.setText(this, amount?.formatCurrency(currency))
