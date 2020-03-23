package org.ccci.gto.android.common.androidx.collection

import android.os.Parcel
import androidx.collection.keyIterator
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongSparseBooleanArrayRobolectricTest {
    @Test
    fun verifyIsParcelable() {
        val orig = LongSparseBooleanArray().apply {
            put(1L, true)
            put(2003L, false)
            put(Long.MAX_VALUE, true)
        }

        val parcel = Parcel.obtain()
        orig.writeToParcel(parcel, orig.describeContents())

        parcel.setDataPosition(0)
        val created = LongSparseBooleanArray.create(parcel)

        assertThat(created.keyIterator().asSequence().toList(), contains(1L, 2003L, Long.MAX_VALUE))
        assertTrue(created.get(1L))
        assertFalse(created.get(2003L))
        assertTrue(created.get(Long.MAX_VALUE))
    }
}
