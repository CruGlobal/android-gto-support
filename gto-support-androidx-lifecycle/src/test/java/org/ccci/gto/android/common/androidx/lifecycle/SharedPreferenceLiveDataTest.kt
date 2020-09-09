package org.ccci.gto.android.common.androidx.lifecycle

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val KEY1 = "key1"
private const val KEY2 = "key2"

@RunWith(AndroidJUnit4::class)
class SharedPreferenceLiveDataTest : BaseLiveDataTest() {
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        prefs = getApplicationContext<Context>().getSharedPreferences("livedata", MODE_PRIVATE)
    }

    @Test
    fun verifyBooleanLiveData() {
        val liveData = prefs.getBooleanLiveData(KEY1, true)

        // initial value
        liveData.observeForever(observer)
        verify(observer).onChanged(true)
        assertTrue(liveData.value!!)
        reset(observer)

        // update key
        prefs.edit { putBoolean(KEY1, false) }
        verify(observer).onChanged(false)
        assertFalse(liveData.value!!)
        reset(observer)

        // update unrelated key
        prefs.edit { putBoolean(KEY2, false) }
        verify(observer, never()).onChanged(any())

        // remove key
        prefs.edit { remove(KEY1) }
        verify(observer).onChanged(true)
        assertTrue(liveData.value!!)
    }
}
