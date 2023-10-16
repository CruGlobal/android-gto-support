package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.app.Activity
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.recyclerView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
class PageTransformerFixTest {
    @get:Rule
    val activityScenario = ActivityScenarioRule(Activity::class.java)
    private lateinit var looper: ShadowLooper

    private lateinit var pager: ViewPager2
    private val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            setHasStableIds(true)
        }

        var items = Array(2) { it }
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemId(position: Int) = items[position].toLong()
        override fun getItemCount() = items.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(
            View(parent.context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            },
        ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }

    @Before
    fun setup() {
        activityScenario.scenario.onActivity {
            pager = ViewPager2(it).apply {
                adapter = this@PageTransformerFixTest.adapter
                offscreenPageLimit = 3
                setPageTransformer { v, pos -> v.translationZ = pos }
            }
            it.setContentView(pager)
        }
        looper = Shadows.shadowOf(Looper.getMainLooper())
    }

    @Test
    fun verifyBrokenPageTransformerBehavior() {
        assertPageTransform()
        adapter.items.reverse()
        adapter.notifyDataSetChanged()
        assertPageTransform(invalid = true)
    }

    @Test
    fun verifyPageTransformerFixBehavior() {
        pager.registerPageTransformerFix(adapter)

        assertPageTransform()
        adapter.items.reverse()
        adapter.notifyDataSetChanged()
        assertPageTransform()
    }

    private fun assertPageTransform(invalid: Boolean = false) {
        looper.idle()
        assertFalse(pager.recyclerView.hasPendingAdapterUpdates())
        if (!invalid) {
            assertEquals(0f, lookupTranslationZFor(0), 0.0001f)
            assertEquals(1f, lookupTranslationZFor(1), 0.0001f)
        } else {
            assertNotEquals(0f, lookupTranslationZFor(0), 0.0001f)
            assertNotEquals(1f, lookupTranslationZFor(1), 0.0001f)
        }
    }

    private fun lookupTranslationZFor(pos: Int) =
        pager.recyclerView.findViewHolderForAdapterPosition(pos)!!.itemView.translationZ
}
