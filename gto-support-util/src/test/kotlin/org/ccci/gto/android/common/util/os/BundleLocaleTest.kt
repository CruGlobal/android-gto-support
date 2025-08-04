package org.ccci.gto.android.common.util.os

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test
import org.junit.runner.RunWith

private const val KEY1 = "key1"
private const val KEY2 = "key2"
private const val KEY3 = "key3"

@RunWith(AndroidJUnit4::class)
class BundleLocaleTest {
    @Test
    fun verifyPutLocaleAndGetLocale() {
        val bundle = Bundle().apply {
            putLocale(KEY1, Locale.ENGLISH)
            putLocale(KEY2, Locale.FRENCH)
            putLocale(KEY3, null)
        }

        assertEquals(3, bundle.size())
        assertEquals(Locale.ENGLISH, bundle.getLocale(KEY1))
        assertEquals(Locale.FRENCH, bundle.getLocale(KEY2))
        assertNull(bundle.getLocale(KEY3))
    }
}
