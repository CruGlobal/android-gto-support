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
class HsvColorTest {
    @Test
    fun verifyColorToHsvColor() {
        assertHsvEquals(HSV_CYAN, Color.CYAN.toHsvColor())
        assertHsvEquals(HSV_LTGRAY, Color.LTGRAY.toHsvColor())
        assertHsvEquals(HSV_WHITE, Color.WHITE.toHsvColor())
    }

    @Test
    fun verifyHsvToColorInt() {
        assertEquals(Color.CYAN, HSV_CYAN.toColorInt())
        assertEquals(Color.LTGRAY, HSV_LTGRAY.toColorInt())
        assertEquals(Color.WHITE, HSV_WHITE.toColorInt())
    }

    @Test
    fun verifyToHsl() {
        assertEquals(HSL_CYAN, HSV_CYAN.toHslColor())
        assertEquals(HSL_LTGRAY, HSV_LTGRAY.toHslColor())
        assertEquals(HSL_WHITE, HSV_WHITE.toHslColor())
    }

    @Test
    fun verifyColorHsvRoundTrip() {
        val rand = SecureRandom.getInstanceStrong()
        val color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
        assertEquals(color, color.toHsvColor().toColorInt())
    }
}
