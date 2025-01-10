package org.ccci.gto.android.common.firebase.remoteconfig

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

private fun <T> FirebaseRemoteConfig.configFlow(block: () -> T) = callbackFlow {
    val registration = addOnConfigUpdateListener(
        object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) = schedule { trySendBlocking(block()) }
            override fun onError(error: FirebaseRemoteConfigException) = Unit
        }
    )

    trySend(block())

    awaitClose { registration.remove() }
}

fun FirebaseRemoteConfig.getBooleanFlow(key: String) = configFlow { getBoolean(key) }
fun FirebaseRemoteConfig.getDoubleFlow(key: String) = configFlow { getDouble(key) }
fun FirebaseRemoteConfig.getLongFlow(key: String) = configFlow { getLong(key) }
fun FirebaseRemoteConfig.getStringFlow(key: String) = configFlow { getString(key) }
