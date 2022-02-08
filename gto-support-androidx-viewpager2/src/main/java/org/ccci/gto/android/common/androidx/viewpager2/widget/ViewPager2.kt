package org.ccci.gto.android.common.androidx.viewpager2.widget

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.ccci.gto.android.common.androidx.viewpager2.R

/**
 * Provides a work-around for https://issuetracker.google.com/issues/181785654
 */
@UiThread
fun ViewPager2.whileMaintainingVisibleCurrentItem(block: (RecyclerView.Adapter<*>?) -> Unit) {
    val adapter = adapter
    val visible = adapter?.takeIf { it.hasStableIds() }?.takeIf { it.itemCount > currentItem }?.getItemId(currentItem)
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

@Suppress("UNCHECKED_CAST")
val ViewPager2.currentItemLiveData: LiveData<Int>
    get() = getTag(R.id.viewpager2_livedata_currentitem) as? LiveData<Int> ?: object : LiveData<Int>(currentItem) {
        val callbacks = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateValue()
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = updateValue()
            override fun onPageScrollStateChanged(state: Int) = updateValue()
        }

        private fun updateValue() {
            if (currentItem != value) value = currentItem
        }

        override fun onActive() {
            registerOnPageChangeCallback(callbacks)
            updateValue()
        }

        override fun onInactive() {
            unregisterOnPageChangeCallback(callbacks)
        }
    }.also { setTag(R.id.viewpager2_livedata_currentitem, it) }
