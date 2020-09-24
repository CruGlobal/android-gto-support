package org.ccci.gto.android.common.androidx.collection

import android.graphics.Point
import android.os.Parcel
import androidx.collection.keyIterator
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongSparseParcelableArrayRobolectricTest {
    @Test
    fun verifyIsParcelable() {
        val orig = LongSparseParcelableArray<Point>().apply {
            put(1, Point(1, -1))
            put(2003, Point(2003, -2003))
            put(Long.MAX_VALUE, Point(Int.MAX_VALUE, Int.MIN_VALUE))
        }

        val parceledBytes = Parcel.obtain().run {
            writeParcelable(orig, 0)
            marshall()
        }

        val created = Parcel.obtain().run {
            unmarshall(parceledBytes, 0, parceledBytes.size)
            setDataPosition(0)
            readParcelable<LongSparseParcelableArray<Point>>(this::class.java.classLoader)!!
        }

        assertThat(created.keyIterator().asSequence().toList(), contains(1L, 2003L, Long.MAX_VALUE))
        assertEquals(Point(1, -1), created.get(1))
        assertEquals(Point(2003, -2003), created.get(2003))
        assertEquals(Point(Int.MAX_VALUE, Int.MIN_VALUE), created.get(Long.MAX_VALUE))
    }
}
