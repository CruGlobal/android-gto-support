package org.ccci.gto.android.common.viewpager.widget

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.karumi.weak.weak

class SwipeRefreshLayoutViewPagerHelper(layout: SwipeRefreshLayout? = null) : ViewPager.SimpleOnPageChangeListener() {
    private val swipeRefreshLayout: SwipeRefreshLayout? by weak(layout)

    private var _isRefreshing: Boolean? = null
    var isRefreshing: Boolean
        get() = _isRefreshing ?: swipeRefreshLayout?.isRefreshing ?: false
        set(value) {
            if (isIdle) {
                swipeRefreshLayout?.isRefreshing = value
            } else {
                _isRefreshing = value
            }
        }

    private var isIdle = true
        set(value) {
            if (value == field) return
            field = value
            _isRefreshing?.let {
                _isRefreshing = null
                isRefreshing = it
            }
        }

    override fun onPageScrollStateChanged(state: Int) {
        swipeRefreshLayout?.apply {
            when {
                state == ViewPager.SCROLL_STATE_IDLE -> isEnabled = true
                !isRefreshing -> isEnabled = false
            }
        }
        isIdle = state == ViewPager.SCROLL_STATE_IDLE
    }
}

@Deprecated("Since v3.0.0, use SwipeRefreshLayoutViewPagerHelper instead")
class SwipeRefreshLayoutOnPageChangeListener(layout: SwipeRefreshLayout? = null) :
    ViewPager.OnPageChangeListener by SwipeRefreshLayoutViewPagerHelper(layout)
