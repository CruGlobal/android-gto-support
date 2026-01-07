package org.ccci.gto.android.common.androidx.viewpager2.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.viewpager2.widget.ViewPager2
import org.ccci.gto.android.common.androidx.viewpager2.R

private const val ON_PAGE_SCROLLED = "onPageScrolled"
private const val ON_PAGE_SELECTED = "onPageSelected"

@BindingAdapter(ON_PAGE_SCROLLED, ON_PAGE_SELECTED, requireAll = false)
internal fun ViewPager2.setOnPageChangeCallback(scrolled: OnPageScrolled?, selected: OnPageSelected?) {
    val newValue = when {
        scrolled == null && selected == null -> null

        else -> object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                scrolled?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                selected?.onPageSelected(position)
            }
        }
    }

    ListenerUtil.trackListener(this, newValue, R.id.viewpager2_databinding_pagechangecallback)
        ?.let { unregisterOnPageChangeCallback(it) }
    newValue?.let { registerOnPageChangeCallback(it) }
}

internal interface OnPageScrolled {
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
}

internal interface OnPageSelected {
    fun onPageSelected(position: Int)
}
