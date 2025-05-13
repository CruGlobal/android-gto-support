package org.ccci.gto.android.common.compat.os

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

private const val KEY1 = "key1"
private const val KEY2 = "key2"

@RunWith(AndroidJUnit4::class)
@Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.S_V2, Build.VERSION_CODES.TIRAMISU, NEWEST_SDK])
class BundleCompatTest {
    @Test
    fun verifyGetParcelable() {
        val point = Point(0, 0)
        val bundle = Bundle().apply {
            putParcelable(KEY1, point)
            putParcelable(KEY2, null)
        }

        val resp = bundle.getParcelableCompat(KEY1, Point::class.java)!!
        assertEquals(point.javaClass, resp.javaClass)
        assertNull(bundle.getParcelableCompat(KEY2, Point::class.java))
    }

    @Test
    fun verifyGetParcelableArray() {
        val points = arrayOf(Point(0, 0), null, Point(1, 1))
        val bundle = Bundle().apply {
            putParcelableArray(KEY1, points)
            putParcelableArray(KEY2, null)
        }

        val resp = bundle.getParcelableArrayCompat(KEY1, Point::class.java)!!
        assertEquals(points.javaClass, resp.javaClass)
        assertContentEquals(points, resp)
        assertNull(bundle.getParcelableArrayCompat(KEY2, Point::class.java))
    }

    @Test
    fun verifyGetSerializable() {
        val date = LocalDate.now()
        val bundle = Bundle().apply {
            putSerializable(KEY1, date)
            putSerializable(KEY2, null)
        }

        assertNull(bundle.getSerializableCompat(KEY1, Instant::class.java))
        assertNotNull(bundle.getSerializableCompat(KEY1, LocalDate::class.java)) {
            assertEquals(date, it)
            assertEquals(date.javaClass, it.javaClass)
        }
        assertNull(bundle.getSerializableCompat(KEY2, LocalDate::class.java))
    }
}
