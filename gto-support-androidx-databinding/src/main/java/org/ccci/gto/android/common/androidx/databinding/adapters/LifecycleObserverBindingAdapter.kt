package org.ccci.gto.android.common.androidx.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.ccci.gto.android.common.androidx.databinding.R

@BindingAdapter("lifecycleOwner")
internal fun View.observeLifecycle(owner: LifecycleOwner?) {
    if (this !is LifecycleObserver) return

    val old = ListenerUtil.trackListener(this, owner, R.id.gto_support_databinding_lifecycleOwner)
    if (old !== owner) {
        old?.lifecycle?.removeObserver(this)
        owner?.lifecycle?.addObserver(this)
    }
}
