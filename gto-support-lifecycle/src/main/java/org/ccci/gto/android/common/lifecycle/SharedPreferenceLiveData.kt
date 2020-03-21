package org.ccci.gto.android.common.lifecycle

import android.content.SharedPreferences
import org.ccci.gto.android.common.androidx.lifecycle.getBooleanLiveData
import org.ccci.gto.android.common.androidx.lifecycle.getFloatLiveData
import org.ccci.gto.android.common.androidx.lifecycle.getIntLiveData
import org.ccci.gto.android.common.androidx.lifecycle.getLongLiveData
import org.ccci.gto.android.common.androidx.lifecycle.getStringLiveData
import org.ccci.gto.android.common.androidx.lifecycle.getStringSetLiveData

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getBooleanLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getBooleanLiveData"
    )
)
fun SharedPreferences.getBooleanLiveData(key: String, defValue: Boolean) = getBooleanLiveData(key, defValue)

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getFloatLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getFloatLiveData"
    )
)
fun SharedPreferences.getFloatLiveData(key: String, defValue: Float) = getFloatLiveData(key, defValue)

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getIntLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getIntLiveData"
    )
)
fun SharedPreferences.getIntLiveData(key: String, defValue: Int) = getIntLiveData(key, defValue)

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getLongLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getLongLiveData"
    )
)
fun SharedPreferences.getLongLiveData(key: String, defValue: Long) = getLongLiveData(key, defValue)

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getStringLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getStringLiveData"
    )
)
fun SharedPreferences.getStringLiveData(key: String, defValue: String?) = getStringLiveData(key, defValue)

@Deprecated(
    "Since v3.4.0, use gto-support-androidx-lifecycle instead", ReplaceWith(
        "getStringSetLiveData(key, defValue)",
        "org.ccci.gto.android.common.androidx.lifecycle.getStringSetLiveData"
    )
)
fun SharedPreferences.getStringSetLiveData(key: String, defValue: Set<String>?) = getStringSetLiveData(key, defValue)
