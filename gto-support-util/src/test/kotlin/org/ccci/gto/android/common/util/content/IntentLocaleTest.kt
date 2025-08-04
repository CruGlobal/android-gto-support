package org.ccci.gto.android.common.util.content

import android.content.Intent
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test
import org.junit.runner.RunWith

private const val KEY1 = "key1"
private const val KEY2 = "key2"
private const val KEY3 = "key3"
private const val KEY4 = "key4"

@RunWith(AndroidJUnit4::class)
class IntentLocaleTest {
    @Test
    fun testLocaleRoundtrip() {
        val intent = Intent(Intent.ACTION_SEND)
            .putExtra(KEY1, Locale.ENGLISH)
            .putExtra(KEY2, Locale.FRANCE, true)
            .putExtra(KEY3, Locale.GERMAN, false)
            .putExtra(KEY4, null as Locale?)

        val parceledBytes = Parcel.obtain().run {
            writeParcelable(intent, 0)
            marshall()
        }

        val copy = Parcel.obtain().run {
            unmarshall(parceledBytes, 0, parceledBytes.size)
            setDataPosition(0)
            readParcelable<Intent>(this::class.java.classLoader)!!
        }

        assertEquals(Locale.ENGLISH, copy.getLocaleExtra(KEY1))
        assertEquals(Locale.FRANCE, copy.getLocaleExtra(KEY2))
        assertEquals(Locale.GERMAN, copy.getLocaleExtra(KEY3))
        assertNull(copy.getLocaleExtra(KEY4))
    }
}
