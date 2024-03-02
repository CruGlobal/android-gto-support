package org.ccci.gto.android.common.util.os

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Assert.assertFalse
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NEWEST_SDK
import org.robolectric.annotation.Config.OLDEST_SDK

private const val KEY1 = "key1"
private const val KEY2 = "key2"

@RunWith(AndroidJUnit4::class)
class BundleTest {
    // region equalsBundle
    @Test
    fun `equalsBundle()`() {
        val bundle = Bundle().apply {
            putString("a", "b")
            putString("b", "a")
        }

        assertTrue(null equalsBundle null)
        assertTrue(bundle equalsBundle bundle)
        assertTrue(bundle equalsBundle (bundle.clone() as Bundle))
        assertFalse(bundle equalsBundle null)
        assertFalse(null equalsBundle bundle)
        assertFalse(bundle equalsBundle Bundle())
        assertFalse(Bundle() equalsBundle bundle)
    }
    // endregion equalsBundle

    // region ParcelableArrays
    @Test
    @Config(sdk = [OLDEST_SDK, Build.VERSION_CODES.S_V2, Build.VERSION_CODES.TIRAMISU, NEWEST_SDK])
    fun verifyGetParcelableArrayReified() {
        val points = arrayOf(Point(0, 0), null, Point(1, 1))
        val bundle = Bundle().apply {
            putParcelableArray(KEY1, points)
            putParcelableArray(KEY2, null)
        }

        val resp = bundle.getTypedParcelableArray<Point>(KEY1)!!
        assertEquals(points.javaClass, resp.javaClass)
        assertContentEquals(points, resp)
        assertNull(bundle.getTypedParcelableArray<Point>(KEY2))
    }
    // endregion ParcelableArrays
}
