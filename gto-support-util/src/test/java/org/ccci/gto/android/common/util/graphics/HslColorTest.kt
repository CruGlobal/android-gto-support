package org.ccci.gto.android.common.util.graphics

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.security.SecureRandom
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class HslColorTest {
    @Test
    fun verifyToHsv() {
        assertHsvEquals(HSV_CYAN, HSL_CYAN.toHsvColor())
        assertHsvEquals(HSV_LTGRAY, HSL_LTGRAY.toHsvColor())
        assertHsvEquals(HSV_WHITE, HSL_WHITE.toHsvColor())
    }

    @Test
    fun verifyToColorInt() {
        assertEquals(Color.CYAN, HSL_CYAN.toColorInt())
        assertEquals(Color.LTGRAY, HSL_LTGRAY.toColorInt())
        assertEquals(Color.WHITE, HSL_WHITE.toColorInt())
    }

    @Test
    fun verifyFromColorInt() {
        assertEquals(HSL_CYAN, Color.CYAN.toHslColor())
        assertEquals(HSL_LTGRAY, Color.LTGRAY.toHslColor())
        assertEquals(HSL_WHITE, Color.WHITE.toHslColor())
    }

    @Test
    fun verifyColorHslRoundTrip() {
        val rand = SecureRandom.getInstanceStrong()
        val color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
        assertEquals(color, color.toHslColor().toColorInt())
    }

    @Test
    fun verifyHsvHslRoundtrip() {
        val rand = SecureRandom.getInstanceStrong()
        val hsv = HsvColor(rand.nextFloat() * 360, rand.nextFloat(), rand.nextFloat())
        assertHsvEquals(hsv, hsv.toHslColor().toHsvColor())
    }

    @Test
    fun verifyDarken() {
        assertEquals("white darkened 100% is black", Color.BLACK, Color.WHITE.toHslColor().darken(1f).toColorInt())
        assertEquals(Color.rgb(194, 0, 0), Color.RED.toHslColor().darken(.12f).toColorInt())
    }
}
