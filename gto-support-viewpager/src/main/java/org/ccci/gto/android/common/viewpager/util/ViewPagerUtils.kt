@file:JvmName("ViewPagerUtils")

package org.ccci.gto.android.common.viewpager.util

import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import timber.log.Timber

fun ViewPager.setScroller(scroller: Scroller) {
    try {
        ViewPager::class.java.getField("mScroller")
            .apply { isAccessible = true }
            .set(this, scroller)
    } catch (e: Exception) {
        Timber.tag("ViewPagerUtils")
            .e(e, "Error setting custom scroller on ViewPager")
    }
}
