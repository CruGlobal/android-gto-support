package org.ccci.gto.android.common.recyclerview.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.ccci.gto.android.common.androidx.recyclerview.util.lifecycleOwner

@Deprecated("Since v3.11.2, use lifecycleOwner from gto-support-androidx-recyclerview instead.")
@get:MainThread
@set:MainThread
var RecyclerView.lifecycleOwner: LifecycleOwner?
    get() = lifecycleOwner
    set(value) {
        lifecycleOwner = value
    }
