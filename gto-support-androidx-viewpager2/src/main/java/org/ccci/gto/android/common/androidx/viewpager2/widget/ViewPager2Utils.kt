package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.recyclerView

@JvmOverloads
fun ViewPager2.setHeightWrapContent(pagesHaveConsistentHeight: Boolean = false) {
    val recyclerView = recyclerView

    with(recyclerView) {
        // update nested RecyclerView to have a wrap_content height
        layoutParams = layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }

        // remove restriction that children should have match_parent for height and width
        // source: https://gist.github.com/safaorhan/1a541af729c7657426138d18b87d5bd4
        clearOnChildAttachStateChangeListeners()
    }

    if (!pagesHaveConsistentHeight) {
        // disable the measurement cache because it breaks differently sized pages that have identical layout params
        recyclerView.layoutManager!!.isMeasurementCacheEnabled = false

        // request layout whenever we scroll in case the height of new pages will change the current height measurement
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) = recyclerView.requestLayout()
        })
    }
}
