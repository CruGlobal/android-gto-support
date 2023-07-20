@file:BindingMethods(
    BindingMethod(type = MaterialButtonToggleGroup::class, method = "check", attribute = CHECKED_BUTTON_ID)
)
@file:InverseBindingMethods(
    InverseBindingMethod(
        type = MaterialButtonToggleGroup::class,
        method = "getCheckedButtonId",
        attribute = CHECKED_BUTTON_ID,
        event = CHECKED_BUTTON_ATTR
    )
)

package org.ccci.gto.android.common.material.button.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import androidx.databinding.adapters.ListenerUtil
import com.google.android.material.button.MaterialButtonToggleGroup
import org.ccci.gto.android.common.material.components.R

private const val CHECKED_BUTTON_ID = "checkedButtonId"
private const val CHECKED_BUTTON_ATTR = "checkedButtonAttr"

@BindingAdapter(CHECKED_BUTTON_ATTR)
internal fun MaterialButtonToggleGroup.setCheckedButtonBindingListener(listener: InverseBindingListener?) =
    listener?.let { MaterialButtonToggleGroup.OnButtonCheckedListener { _, _, _ -> listener.onChange() } }
        .also {
            ListenerUtil.trackListener(this, it, R.id.gto_support_MaterialButtonToggleGroup_checkedButtonAttr)
                ?.let { old -> removeOnButtonCheckedListener(old) }
        }
        ?.let { addOnButtonCheckedListener(it) }
