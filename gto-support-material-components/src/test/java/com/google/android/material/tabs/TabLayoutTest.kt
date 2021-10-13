package com.google.android.material.tabs

import org.ccci.gto.android.common.material.tabs.notifyPagerAdapterChanged
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class TabLayoutTest {
    @Test
    fun testNotifyPagerAdapterChangedTriggersPopulateFromPagerAdapter() {
        val tabs: TabLayout = mock()

        tabs.notifyPagerAdapterChanged()
        verify(tabs).populateFromPagerAdapter()
    }
}
