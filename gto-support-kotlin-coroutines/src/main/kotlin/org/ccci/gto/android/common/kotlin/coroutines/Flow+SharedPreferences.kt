package org.ccci.gto.android.common.kotlin.coroutines

import android.content.SharedPreferences
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

private val UNSPECIFIED = String()

fun SharedPreferences.getChangeFlow(initialKey: String? = UNSPECIFIED) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k -> trySendBlocking(k) }
    registerOnSharedPreferenceChangeListener(listener)
    if (initialKey !== UNSPECIFIED) send(initialKey)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.buffer(UNLIMITED)

fun SharedPreferences.getBooleanFlow(key: String, defValue: Boolean) = getFlow(key) { getBoolean(key, defValue) }
fun SharedPreferences.getFloatFlow(key: String, defValue: Float) = getFlow(key) { getFloat(key, defValue) }
fun SharedPreferences.getIntFlow(key: String, defValue: Int) = getFlow(key) { getInt(key, defValue) }
fun SharedPreferences.getLongFlow(key: String, defValue: Long) = getFlow(key) { getLong(key, defValue) }
fun SharedPreferences.getStringFlow(key: String, defValue: String?) = getFlow(key) { getString(key, defValue) }
fun SharedPreferences.getStringSetFlow(key: String, defValue: Set<String>?) =
    getFlow(key) { getStringSet(key, defValue) }

private fun <T> SharedPreferences.getFlow(key: String, value: () -> T) = getChangeFlow(initialKey = null)
    .filter { it == null || it == key }
    .map { value() }
    .conflate()
