package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, NEWEST_SDK])
class ViewPager2HeightWrapContentTest {
    private lateinit var framelayout: FrameLayout
    private lateinit var viewpager: ViewPager2

    @Before
    fun setUp() {
        framelayout = FrameLayout(ApplicationProvider.getApplicationContext())
        framelayout.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

        viewpager = ViewPager2(framelayout.context)
        viewpager.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        framelayout.addView(viewpager)
    }

    @Test(expected = IllegalStateException::class)
    fun verifyDefaultBehaviorCrashes() {
        viewpager.adapter = WrapContentChildren(50)
        triggerMeasure()
        fail("default behavior of ViewPager2 has changed")
    }

    @Test
    fun verifyChildrenWithWrapContentDoesntCrashAndMeasuresCorrectly() {
        viewpager.setHeightWrapContent()
        viewpager.adapter = WrapContentChildren(50, 50, 100)
        triggerMeasure()
        assertEquals(50, viewpager.measuredHeight)
        viewpager.setCurrentItem(1, false)
        triggerMeasure()
        assertEquals(50, viewpager.measuredHeight)
        viewpager.setCurrentItem(2, false)
        triggerMeasure()
        assertEquals(100, viewpager.measuredHeight)
    }

    private fun triggerMeasure(
        width: Int = makeMeasureSpec(2000, View.MeasureSpec.EXACTLY),
        height: Int = makeMeasureSpec(5000, View.MeasureSpec.EXACTLY)
    ) = framelayout.measure(width, height)

    class WrapContentChildren(private vararg val heights: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = heights.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : RecyclerView.ViewHolder(parent.createView()) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as? FrameLayout)?.getChildAt(0)?.apply {
                (this as? TextView)?.text = "$position"
                layoutParams = layoutParams.apply { height = heights[position] }
            }
        }
    }
}

private fun ViewGroup.createView() = FrameLayout(context).apply {
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    addView(TextView(context).apply { layoutParams = LayoutParams(MATCH_PARENT, 0) })
}
