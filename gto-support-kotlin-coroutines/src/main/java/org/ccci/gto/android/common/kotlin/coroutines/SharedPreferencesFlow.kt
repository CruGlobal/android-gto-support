package org.ccci.gto.android.common.kotlin.coroutines

import android.content.SharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

@ExperimentalCoroutinesApi
fun SharedPreferences.getBooleanFlow(key: String, defValue: Boolean) = getFlow(key) { getBoolean(key, defValue) }

@ExperimentalCoroutinesApi
fun SharedPreferences.getFloatFlow(key: String, defValue: Float) = getFlow(key) { getFloat(key, defValue) }

@ExperimentalCoroutinesApi
fun SharedPreferences.getIntFlow(key: String, defValue: Int) = getFlow(key) { getInt(key, defValue) }

@ExperimentalCoroutinesApi
fun SharedPreferences.getLongFlow(key: String, defValue: Long) = getFlow(key) { getLong(key, defValue) }

@ExperimentalCoroutinesApi
fun SharedPreferences.getStringFlow(key: String, defValue: String?) = getFlow(key) { getString(key, defValue) }

@ExperimentalCoroutinesApi
fun SharedPreferences.getStringSetFlow(key: String, defValue: Set<String>?) =
    getFlow(key) { getStringSet(key, defValue) }

@ExperimentalCoroutinesApi
private fun <T> SharedPreferences.getFlow(key: String, value: () -> T) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (k == null || k == key) trySend(value())
    }

    registerOnSharedPreferenceChangeListener(listener)
    trySend(value())
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.conflate()
