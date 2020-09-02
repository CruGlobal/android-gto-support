package org.ccci.gto.android.common.util.os

import android.graphics.Point
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private const val KEY1 = "key1"
private const val KEY2 = "key2"

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class BundleTest {
    @Test
    fun verifyGetParcelableArray() {
        val points = arrayOf(Point(0, 0), null, Point(1, 1))
        val bundle = Bundle().apply {
            putParcelableArray(KEY1, points)
            putParcelableArray(KEY2, null)
        }

        val resp = bundle.getParcelableArray(KEY1, Point::class.java)!!
        assertEquals(points.javaClass, resp.javaClass)
        assertArrayEquals(points, resp)
        assertNull(bundle.getParcelableArray(KEY2, Point::class.java))
    }

    @Test
    fun verifyGetParcelableArrayReified() {
        val points = arrayOf(Point(0, 0), null, Point(1, 1))
        val bundle = Bundle().apply {
            putParcelableArray(KEY1, points)
            putParcelableArray(KEY2, null)
        }

        val resp = bundle.getTypedParcelableArray<Point>(KEY1)!!
        assertEquals(points.javaClass, resp.javaClass)
        assertArrayEquals(points, resp)
        assertNull(bundle.getTypedParcelableArray<Point>(KEY2))
    }
}
