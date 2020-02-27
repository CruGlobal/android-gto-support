package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.recyclerView

fun ViewPager2.setHeightWrapContent() {
    with(recyclerView) {
        // update nested RecyclerView to have a wrap_content height
        layoutParams = layoutParams.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }

        // remove restriction that children should have match_parent for height and width
        // source: https://gist.github.com/safaorhan/1a541af729c7657426138d18b87d5bd4
        clearOnChildAttachStateChangeListeners()
    }
}
