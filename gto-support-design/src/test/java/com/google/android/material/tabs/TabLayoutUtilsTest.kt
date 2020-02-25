package com.google.android.material.tabs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

class TabLayoutUtilsTest {
    @Test
    fun testNotifyPagerAdapterChangedTriggersPopulateFromPagerAdapter() {
        val tabs: TabLayout = mock()

        tabs.notifyPagerAdapterChanged()
        verify(tabs).populateFromPagerAdapter()
    }
}
