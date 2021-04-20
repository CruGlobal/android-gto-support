package org.ccci.gto.android.common.androidx.viewpager2.adapter

import android.app.Activity
import android.os.Looper
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [19, 28])
class PrimaryItemChangeObserverTest {
    @get:Rule
    val activityScenario = ActivityScenarioRule(Activity::class.java)

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: TestAdapter

    @Before
    fun setup() {
        activityScenario.scenario.onActivity {
            viewPager = ViewPager2(it).apply { layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) }
            it.setContentView(viewPager)
        }
        adapter = spy(TestAdapter())
    }

    // region register/unregister behavior
    @Test
    fun registerObserver() {
        viewPager.adapter = adapter
        assertNotNull(adapter.observer)
    }

    @Test
    fun `registerObserver - Hooks attached`() {
        val viewPager = mock<ViewPager2>()
        val recyclerView = mock<RecyclerView> { on { parent } doReturn viewPager }

        adapter.onAttachedToRecyclerView(recyclerView)
        val observer = adapter.observer!!
        verify(adapter).registerAdapterDataObserver(observer.dataObserver)
        verify(viewPager).registerOnPageChangeCallback(observer.pageChangeObserver)
        verifyNoMoreInteractions(viewPager)

        adapter.onDetachedFromRecyclerView(recyclerView)
        verify(adapter).unregisterAdapterDataObserver(observer.dataObserver)
        verify(viewPager).unregisterOnPageChangeCallback(observer.pageChangeObserver)
        verifyNoMoreInteractions(viewPager)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `registerObserver - Fails when adapter doesn't have stable ids`() {
        adapter.setHasStableIds(false)
        viewPager.adapter = adapter
    }

    @Test(expected = IllegalStateException::class)
    fun `registerObserver - Fails with regular RecyclerView`() {
        RecyclerView(ApplicationProvider.getApplicationContext()).adapter = adapter
    }
    // endregion register/unregister behavior

    // region updatePrimaryItem()
    @Test
    fun `testUpdatePrimaryItem - Initially Empty`() {
        viewPager.adapter = adapter
        shadowOf(Looper.getMainLooper()).idle()
        verifyNoMoreInteractions(adapter.updatesMock)

        adapter.items = listOf(1, 2)
        adapter.notifyDataSetChanged()
        shadowOf(Looper.getMainLooper()).idle()
        verify(adapter.updatesMock).invoke(argThat { id == 1L }, isNull())
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    @Test
    fun `testUpdatePrimaryItem - Callback for initial item`() {
        adapter.items = listOf(1, 2)
        viewPager.adapter = adapter
        shadowOf(Looper.getMainLooper()).idle()

        verify(adapter.updatesMock).invoke(argThat { id == 1L }, isNull())
        verifyNoMoreInteractions(adapter.updatesMock)
    }
    // endregion updatePrimaryItem()

    class TestAdapter : RecyclerView.Adapter<TestViewHolder>() {
        init {
            setHasStableIds(true)
        }

        var items = emptyList<Long>()

        var observer: PrimaryItemChangeObserver<TestViewHolder>? = null
        val updatesMock: (primaryItem: TestViewHolder?, previousPrimaryItem: TestViewHolder?) -> Unit = mock()

        override fun getItemId(position: Int) = items[position]
        override fun getItemCount() = items.size

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            observer = onUpdatePrimaryItem(recyclerView, updatesMock)
        }
        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            observer?.unregister()
            observer = null
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TestViewHolder(parent)
        override fun onBindViewHolder(holder: TestViewHolder, position: Int) { holder.id = items[position] }
    }

    class TestViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        TextView(parent.context).apply { layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) }
    ) {
        var id: Long? = null
    }
}
