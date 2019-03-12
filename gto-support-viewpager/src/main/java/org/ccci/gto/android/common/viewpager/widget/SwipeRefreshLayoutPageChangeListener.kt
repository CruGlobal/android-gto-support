package org.ccci.gto.android.common.viewpager.widget

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.karumi.weak.weakVar

class SwipeRefreshLayoutPageChangeListener(layout: SwipeRefreshLayout? = null) :
    ViewPager.SimpleOnPageChangeListener() {
    var swipeRefreshLayout: SwipeRefreshLayout? by weakVar()
    init {
        swipeRefreshLayout = layout
    }

    override fun onPageScrollStateChanged(state: Int) {
        swipeRefreshLayout?.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
    }
}
