package org.ccci.gto.android.common.util.content

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Build
import androidx.annotation.RequiresPermission

@RequiresPermission(ACCESS_NETWORK_STATE)
fun Context.isConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        ?.let { it.hasCapability(NET_CAPABILITY_INTERNET) && it.hasCapability(NET_CAPABILITY_VALIDATED) }
        ?: false
}
