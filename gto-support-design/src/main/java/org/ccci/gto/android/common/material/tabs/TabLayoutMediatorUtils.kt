package org.ccci.gto.android.common.material.tabs

import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.populateTabsFromPagerAdapterCompat

fun TabLayoutMediator.notifyChanged() = populateTabsFromPagerAdapterCompat()
