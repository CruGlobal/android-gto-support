package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [16, 17, 21, 28])
class ViewPager2MaintainVisibleItemTest {
    private lateinit var viewpager: ViewPager2
    private lateinit var adapter: ItemsAdapter

    @Before
    fun setUp() {
        viewpager = ViewPager2(ApplicationProvider.getApplicationContext())
        adapter = ItemsAdapter()
    }

    @Test
    fun verifyDefaultBehavior() {
        viewpager.adapter = adapter
        viewpager.setCurrentItem(4, false)
        val expected = adapter.getItemId(viewpager.currentItem)
        adapter.items.removeAt(0)
        adapter.notifyDataSetChanged()
        assertNotEquals(
            "default behavior of ViewPager2 has changed",
            expected,
            adapter.getItemId(viewpager.currentItem)
        )
    }

    @Test
    fun verifyWhileMaintainingVisibleCurrentItem() {
        viewpager.adapter = adapter
        viewpager.setCurrentItem(4, false)
        val expected = adapter.getItemId(viewpager.currentItem)
        viewpager.whileMaintainingVisibleCurrentItem {
            adapter.items.removeAt(0)
            adapter.notifyDataSetChanged()
        }
        assertEquals(expected, adapter.getItemId(viewpager.currentItem))
    }

    @Test
    fun verifyWhileMaintainingVisibleCurrentItemWithoutStableIds() {
        adapter.setHasStableIds(false)
        viewpager.adapter = adapter
        viewpager.setCurrentItem(4, false)
        val unexpected = adapter.getItemId(viewpager.currentItem)
        viewpager.whileMaintainingVisibleCurrentItem {
            adapter.items.removeAt(0)
            adapter.notifyDataSetChanged()
        }
        assertNotEquals(unexpected, adapter.getItemId(viewpager.currentItem))
    }

    @Test
    fun verifyWhileMaintainingVisibleEmptyAdapter() {
        adapter.items.clear()
        viewpager.adapter = adapter
        viewpager.whileMaintainingVisibleCurrentItem {
            adapter.items.add(11L)
            adapter.notifyDataSetChanged()
        }
        assertEquals(11L, adapter.getItemId(viewpager.currentItem))
    }

    private class ItemsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            setHasStableIds(true)
        }

        val items = (1L..10L).toMutableList()

        override fun getItemCount() = items.size
        override fun getItemId(position: Int) = items[position]

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : RecyclerView.ViewHolder(View(parent.context)) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
}
