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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.stubbing.answers.ClonesArguments
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.verification.VerificationMode
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
class PrimaryItemChangeObserverTest {
    @get:Rule
    val activityScenario = ActivityScenarioRule(Activity::class.java)

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: TestAdapter
    private lateinit var looper: ShadowLooper

    @Before
    fun setup() {
        activityScenario.scenario.onActivity {
            viewPager = ViewPager2(it).apply { layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) }
            it.setContentView(viewPager)
        }
        adapter = spy(TestAdapter())
        looper = shadowOf(Looper.getMainLooper())
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
        verify(recyclerView).addOnChildAttachStateChangeListener(observer.childAttachStateChangeListener)
        verify(viewPager).registerOnPageChangeCallback(observer.pageChangeObserver)
        verifyNoMoreInteractions(viewPager)

        adapter.onDetachedFromRecyclerView(recyclerView)
        verify(recyclerView).addOnChildAttachStateChangeListener(observer.childAttachStateChangeListener)
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
        looper.idle()
        verifyNoInteractions(adapter.updatesMock)

        adapter.items = listOf(1, 2)
        adapter.notifyDataSetChanged()
        looper.idle()
        verifyUpdatePrimaryItem(1)
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    @Test
    fun `testUpdatePrimaryItem - Callback for initial item`() {
        adapter.items = listOf(1, 2)
        viewPager.adapter = adapter
        looper.idle()
        verifyUpdatePrimaryItem(1)
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    @Test
    fun `testUpdatePrimaryItem - Clearing items`() {
        adapter.items = listOf(1, 2)
        viewPager.adapter = adapter
        looper.idle()
        verifyUpdatePrimaryItem(1)
        verifyNoMoreInteractions(adapter.updatesMock)

        adapter.items = emptyList()
        adapter.notifyDataSetChanged()
        looper.idle()
        verifyUpdatePrimaryItem(null, 1)
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    @Test
    fun `testUpdatePrimaryItem - Callback when changing current item`() {
        adapter.items = listOf(1, 2)
        viewPager.adapter = adapter
        looper.idle()
        verifyUpdatePrimaryItem(1)
        verifyNoMoreInteractions(adapter.updatesMock)

        viewPager.currentItem = 1
        looper.idle()
        verifyUpdatePrimaryItem(2, 1)
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    @Test
    fun `testUpdatePrimaryItem - Callback when current item is removed`() {
        adapter.items = listOf(1, 2)
        viewPager.adapter = adapter
        looper.idle()
        verifyUpdatePrimaryItem(1)
        verifyNoMoreInteractions(adapter.updatesMock)

        adapter.items = listOf(2)
        adapter.notifyDataSetChanged()
        looper.idle()
        verifyUpdatePrimaryItem(2, 1)
        verifyNoMoreInteractions(adapter.updatesMock)
    }

    private fun verifyUpdatePrimaryItem(
        primaryId: Long?,
        previousPrimaryId: Long? = null,
        mode: VerificationMode = times(1),
    ) = verify(adapter.updatesMock, mode).invoke(
        if (primaryId != null) argThat { id == primaryId } else isNull(),
        if (previousPrimaryId != null) argThat { id == previousPrimaryId } else isNull(),
    )
    // endregion updatePrimaryItem()

    class TestAdapter : RecyclerView.Adapter<TestViewHolder>() {
        init {
            setHasStableIds(true)
        }

        var items = emptyList<Long>()

        var observer: PrimaryItemChangeObserver<TestViewHolder>? = null
        val updatesMock: (primary: TestViewHolder?, previous: TestViewHolder?) -> Unit =
            mock(defaultAnswer = ClonesArguments())

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
        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            holder.id = items[position]
        }
        override fun onViewRecycled(holder: TestViewHolder) {
            holder.id = null
        }
    }

    class TestViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        TextView(parent.context).apply { layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) }
    ) {
        var id: Long? = null
    }
}
