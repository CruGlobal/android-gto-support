package org.ccci.gto.android.common.androidx.lifecycle.net

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map

@RequiresPermission(ACCESS_NETWORK_STATE)
fun Context.isConnectedLiveData(): LiveData<Boolean> = when {
    // TODO: figure out how to detect connected state from the network APIs introduced starting in Lollipop
    else -> ActiveNetworkInfoLiveData(this).map { it?.isConnected == true }.distinctUntilChanged()
}
