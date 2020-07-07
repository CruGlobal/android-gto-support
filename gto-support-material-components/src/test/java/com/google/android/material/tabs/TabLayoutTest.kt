package com.google.android.material.tabs

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.material.tabs.notifyPagerAdapterChanged
import org.junit.Test

class TabLayoutTest {
    @Test
    fun testNotifyPagerAdapterChangedTriggersPopulateFromPagerAdapter() {
        val tabs: TabLayout = mock()

        tabs.notifyPagerAdapterChanged()
        verify(tabs).populateFromPagerAdapter()
    }
}
