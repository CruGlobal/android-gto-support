package org.ccci.gto.android.common.androidx.viewpager2.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [19, 28])
class PrimaryItemChangeObserverTest {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: TestAdapter

    @Before
    fun setup() {
        viewPager = ViewPager2(ApplicationProvider.getApplicationContext())
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

    class TestAdapter : RecyclerView.Adapter<TestViewHolder>() {
        init {
            setHasStableIds(true)
        }

        var items = emptyList<Long>()

        var observer: PrimaryItemChangeObserver<TestViewHolder>? = null
        val updatesMock: (primaryItemId: Long?, previousPrimaryItemId: Long?) -> Unit = mock()

        override fun getItemId(position: Int) = items[position]
        override fun getItemCount() = items.size

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            observer = onUpdatePrimaryItem(recyclerView, updatesMock)
        }
        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            observer?.unregister()
            observer = null
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TestViewHolder(TextView(parent.context))
        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            holder.textView.text = "${items[position]}"
        }
    }

    class TestViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}
