package org.ccci.gto.android.common.androidx.recyclerview.widget

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ConcatAdapter
import org.ccci.gto.android.common.androidx.recyclerview.adapter.SimpleLayoutAdapter

fun ConcatAdapter.addLayout(@LayoutRes layoutId: Int, repeat: Int = 1, initializeLayout: (View) -> Unit = {}) =
    SimpleLayoutAdapter(layoutId, repeat, initializeLayout).also { addAdapter(it) }
