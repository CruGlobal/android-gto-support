package org.ccci.gto.android.common.util.graphics

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.security.SecureRandom

private val HSV_CYAN = HsvColor(180f, 1f, 1f)
private val HSV_LTGRAY = HsvColor(0f, 0f, 0.8f)
private val HSV_WHITE = HsvColor(0f, 0f, 1f)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class HsvColorTest {
    @Test
    fun verifyColorToHsvColor() {
        assertEquals(HSV_CYAN, Color.CYAN.toHsvColor())
        assertEquals(HSV_LTGRAY, Color.LTGRAY.toHsvColor())
        assertEquals(HSV_WHITE, Color.WHITE.toHsvColor())
    }

    @Test
    fun verifyHsvToColorInt() {
        assertEquals(Color.CYAN, HSV_CYAN.toColorInt())
        assertEquals(Color.LTGRAY, HSV_LTGRAY.toColorInt())
        assertEquals(Color.WHITE, HSV_WHITE.toColorInt())
    }

    @Test
    fun verifyHsvRoundTrip() {
        val rand = SecureRandom.getInstanceStrong()
        val color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
        assertEquals(color, color.toHsvColor().toColorInt())
    }

    private fun assertEquals(expected: HsvColor, actual: HsvColor) {
        assertEquals(expected.hue, actual.hue, 0.0000001f)
        assertEquals(expected.saturation, actual.saturation, 0.0000001f)
        assertEquals(expected.value, actual.value, 0.0000001f)
    }
}
