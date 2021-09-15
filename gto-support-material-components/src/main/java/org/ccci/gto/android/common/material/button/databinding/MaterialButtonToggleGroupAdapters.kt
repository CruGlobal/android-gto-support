package org.ccci.gto.android.common.material.button.databinding

import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import com.google.android.material.button.MaterialButtonToggleGroup
import org.ccci.gto.android.common.material.components.R

private const val CHECKED_BUTTON_ID = "checkedButtonId"
private const val CHECKED_BUTTON_ATTR = "checkedButtonAttr"

@get:IdRes
@get:InverseBindingAdapter(attribute = CHECKED_BUTTON_ID, event = CHECKED_BUTTON_ATTR)
@set:BindingAdapter(CHECKED_BUTTON_ID)
internal var MaterialButtonToggleGroup.checkedButtonIdBinding: Int
    get() = checkedButtonId
    set(@IdRes value) = check(value)

@BindingAdapter(CHECKED_BUTTON_ATTR)
internal fun MaterialButtonToggleGroup.setCheckedButtonBindingListener(listener: InverseBindingListener?) =
    listener?.let { MaterialButtonToggleGroup.OnButtonCheckedListener { _, _, _ -> listener.onChange() } }
        .also {
            ListenerUtil.trackListener(this, it, R.id.materialbuttontogglegroup_checkedbuttonlistener)
                ?.let { old -> removeOnButtonCheckedListener(old) }
        }
