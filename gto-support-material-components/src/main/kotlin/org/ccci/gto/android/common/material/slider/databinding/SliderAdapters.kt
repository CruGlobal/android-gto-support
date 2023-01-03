@file:InverseBindingMethods(InverseBindingMethod(type = Slider::class, attribute = "value"))

package org.ccci.gto.android.common.material.slider.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import androidx.databinding.adapters.ListenerUtil
import com.google.android.material.slider.Slider
import org.ccci.gto.android.common.material.components.R

private const val VALUE_ATTR_CHANGED = "valueAttrChanged"

@BindingAdapter(VALUE_ATTR_CHANGED)
internal fun Slider.setValueAttrChangedListener(listener: InverseBindingListener?) =
    listener?.let { Slider.OnChangeListener { _, _, _ -> listener.onChange() } }
        .also {
            ListenerUtil.trackListener(this, it, R.id.gto_support_material_components_Slider_valueAttrChangedListener)
                ?.let { old -> removeOnChangeListener(old) }
        }
        ?.let { addOnChangeListener(it) }
