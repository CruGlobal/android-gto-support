package org.ccci.gto.android.common.recyclerview.advrecyclerview.composedadapter

import android.view.View
import androidx.annotation.LayoutRes
import com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedAdapter
import org.ccci.gto.android.common.androidx.recyclerview.adapter.SimpleLayoutAdapter

fun ComposedAdapter.addLayout(@LayoutRes layoutId: Int, repeat: Int = 1, initializeLayout: (View) -> Unit = {}) =
    addAdapter(SimpleLayoutAdapter(layoutId, repeat, initializeLayout))
