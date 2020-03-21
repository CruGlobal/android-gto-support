package org.ccci.gto.android.common.lifecycle.widget

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import org.ccci.gto.android.common.androidx.lifecycle.widget.ObserverArrayAdapter

@Deprecated("Since v3.4.0, use version in gto-support-androidx-lifecycle instead")
open class ObserverArrayAdapter<T>(context: Context, @LayoutRes layout: Int, @IdRes textViewResourceId: Int = 0) :
    ObserverArrayAdapter<T>(context, layout, textViewResourceId)
