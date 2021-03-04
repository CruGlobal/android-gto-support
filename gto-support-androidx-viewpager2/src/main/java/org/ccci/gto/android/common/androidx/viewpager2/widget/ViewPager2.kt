package org.ccci.gto.android.common.androidx.viewpager2.widget

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Provides a work-around for https://issuetracker.google.com/issues/181785654
 */
fun ViewPager2.whileMaintainingVisibleCurrentItem(block: (RecyclerView.Adapter<*>?) -> Unit) {
    val adapter = adapter
    val visible = adapter?.takeIf { it.hasStableIds() }?.getItemId(currentItem)
    block(adapter)
    if (visible != null) {
        for (pos in 0 until adapter.itemCount) {
            if (adapter.getItemId(pos) == visible) {
                setCurrentItem(pos, false)
                break
            }
        }
    }
}
