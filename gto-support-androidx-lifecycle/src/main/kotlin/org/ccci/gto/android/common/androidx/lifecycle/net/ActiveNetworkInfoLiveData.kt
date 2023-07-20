package org.ccci.gto.android.common.androidx.lifecycle.net

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData

@Suppress("ktlint:standard:annotation")
@SuppressLint("MissingPermission")
internal class ActiveNetworkInfoLiveData @RequiresPermission(ACCESS_NETWORK_STATE) constructor(
    private val context: Context
) : LiveData<NetworkInfo?>() {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onActive() {
        super.onActive()
        context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        value = connectivityManager.activeNetworkInfo
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            value = connectivityManager.activeNetworkInfo
        }
    }
}
