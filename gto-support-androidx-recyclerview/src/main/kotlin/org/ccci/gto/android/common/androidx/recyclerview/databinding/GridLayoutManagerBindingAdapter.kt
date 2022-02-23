package org.ccci.gto.android.common.androidx.recyclerview.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

@BindingAdapter("spanCount")
internal fun RecyclerView.setSpanCount(count: Int) {
    when (val layoutManager = layoutManager) {
        is GridLayoutManager -> layoutManager.spanCount = count
        is StaggeredGridLayoutManager -> layoutManager.spanCount = count
    }
}
