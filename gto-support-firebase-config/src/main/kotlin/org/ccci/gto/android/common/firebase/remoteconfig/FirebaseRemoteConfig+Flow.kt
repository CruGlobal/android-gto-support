package org.ccci.gto.android.common.firebase.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.configUpdates
import kotlinx.coroutines.flow.map

fun FirebaseRemoteConfig.getBooleanFlow(key: String) = configUpdates.map { getBoolean(key) }
fun FirebaseRemoteConfig.getDoubleFlow(key: String) = configUpdates.map { getDouble(key) }
fun FirebaseRemoteConfig.getLongFlow(key: String) = configUpdates.map { getLong(key) }
fun FirebaseRemoteConfig.getStringFlow(key: String) = configUpdates.map { getString(key) }
