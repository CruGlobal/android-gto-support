package org.ccci.gto.android.common.androidx.lifecycle

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

private sealed class SharedPreferenceLiveData<T>(
    protected val prefs: SharedPreferences,
    protected val key: String,
    protected val defValue: T
) : LiveData<T>() {
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == null || key == this.key) value = readValue()
    }

    override fun onActive() {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        value = readValue()
    }

    override fun onInactive() = prefs.unregisterOnSharedPreferenceChangeListener(listener)

    protected abstract fun readValue(): T
}

private class SharedPreferenceBooleanLiveData(prefs: SharedPreferences, key: String, defValue: Boolean) :
    SharedPreferenceLiveData<Boolean>(prefs, key, defValue) {
    override fun readValue() = prefs.getBoolean(key, defValue)
}

private class SharedPreferenceFloatLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Float) :
    SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
    override fun readValue() = prefs.getFloat(key, defValue)
}

private class SharedPreferenceIntLiveData(prefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(prefs, key, defValue) {
    override fun readValue() = prefs.getInt(key, defValue)
}

private class SharedPreferenceLongLiveData(prefs: SharedPreferences, key: String, defValue: Long) :
    SharedPreferenceLiveData<Long>(prefs, key, defValue) {
    override fun readValue() = prefs.getLong(key, defValue)
}

private class SharedPreferenceStringLiveData(sharedPrefs: SharedPreferences, key: String, defValue: String?) :
    SharedPreferenceLiveData<String?>(sharedPrefs, key, defValue) {
    override fun readValue(): String? = prefs.getString(key, defValue)
}

private class SharedPreferenceStringSetLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Set<String>?) :
    SharedPreferenceLiveData<Set<String>?>(sharedPrefs, key, defValue) {
    override fun readValue(): Set<String>? = prefs.getStringSet(key, defValue)
}

fun SharedPreferences.getBooleanLiveData(key: String, defValue: Boolean): LiveData<Boolean> =
    SharedPreferenceBooleanLiveData(this, key, defValue)

fun SharedPreferences.getFloatLiveData(key: String, defValue: Float): LiveData<Float> =
    SharedPreferenceFloatLiveData(this, key, defValue)

fun SharedPreferences.getIntLiveData(key: String, defValue: Int): LiveData<Int> =
    SharedPreferenceIntLiveData(this, key, defValue)

fun SharedPreferences.getLongLiveData(key: String, defValue: Long): LiveData<Long> =
    SharedPreferenceLongLiveData(this, key, defValue)

fun SharedPreferences.getStringLiveData(key: String, defValue: String?): LiveData<String?> =
    SharedPreferenceStringLiveData(this, key, defValue)

fun SharedPreferences.getStringSetLiveData(key: String, defValue: Set<String>?): LiveData<Set<String>?> =
    SharedPreferenceStringSetLiveData(this, key, defValue)
