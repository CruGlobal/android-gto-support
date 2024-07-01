package org.ccci.gto.android.common.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class DimensionUtilsTest {
    private val metrics = DisplayMetrics().apply {
        density = 1f
        scaledDensity = 1f
    }
    private val resources: Resources = mockk {
        every { displayMetrics } returns metrics
    }
    private val context: Context = mockk {
        every { resources } returns this@DimensionUtilsTest.resources
    }

    // region Px
    @Test
    fun `Px - toPixelSize - Rounding`() {
        assertEquals(-2, Px(-1.5).toPixelSize())
        assertEquals(-1, Px(-1.49).toPixelSize())
        assertEquals(-1, Px(-0.1).toPixelSize())
        assertEquals(0, Px(0).toPixelSize())
        assertEquals(1, Px(0.1).toPixelSize())
        assertEquals(1, Px(1.49).toPixelSize())
        assertEquals(2, Px(1.5).toPixelSize())
    }
    // endregion Px

    // region Dp
    @Test
    fun `Dp - toPx`() {
        assertEquals(Px(1), Dp(1).toPx(context))
        metrics.density = 2f
        assertEquals(Px(2), Dp(1).toPx(context))
    }

    @Test
    fun `Dp - toPixelSize`() {
        metrics.density = 2.5f
        assertEquals(5, Dp(2).toPixelSize(context))
    }
    // endregion Dp
}
