package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.app.Activity
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.ALL_SDKS

@RunWith(AndroidJUnit4::class)
@Config(sdk = [ALL_SDKS])
class ViewPager2HeightWrapContentTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var framelayout: FrameLayout
    private lateinit var viewpager: ViewPager2

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        framelayout = FrameLayout(activity)
        framelayout.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        activity.setContentView(framelayout)

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
    fun verifyChildrenWithWrapContentDoesntCrash() {
        viewpager.setHeightWrapContent()
        viewpager.adapter = WrapContentChildren(50)
        triggerMeasure()
        assertEquals(50, viewpager.measuredHeight)
    }

    private fun triggerMeasure(
        width: Int = makeMeasureSpec(2000, View.MeasureSpec.EXACTLY),
        height: Int = makeMeasureSpec(5000, View.MeasureSpec.EXACTLY)
    ) = framelayout.measure(width, height)

    class WrapContentChildren(private vararg val heights: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = heights.size
        override fun getItemViewType(position: Int) = position
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(createView(parent, heights[viewType], MATCH_PARENT)) {}
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
}

private fun createView(parent: ViewGroup, height: Int, width: Int) = TextView(parent.context).apply {
    layoutParams = LayoutParams(width, height)
}
