package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK, Config.NEWEST_SDK])
class ViewPager2CurrentItemLiveDataTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var viewpager: ViewPager2
    private val observer = mock<Observer<Int>>()

    @Before
    fun setupMocks() {
        viewpager = ViewPager2(ApplicationProvider.getApplicationContext()).apply {
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun getItemCount() = 10
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    object : RecyclerView.ViewHolder(View(parent.context)) {}
                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
            }
        }
    }

    @Test
    fun testLiveDataHandleShared() {
        val livedata1 = viewpager.currentItemLiveData
        val livedata2 = viewpager.currentItemLiveData
        assertSame(livedata1, livedata2)
    }

    @Test
    fun testUpdatingCurrentItem() {
        val livedata = viewpager.currentItemLiveData
        livedata.observeForever(observer)
        verify(observer).onChanged(0)
        verifyNoMoreInteractions(observer)
        assertEquals(0, livedata.value)

        viewpager.currentItem = 5
        verify(observer).onChanged(5)
        verifyNoMoreInteractions(observer)
        assertEquals(5, livedata.value)

        livedata.removeObserver(observer)
    }
}
