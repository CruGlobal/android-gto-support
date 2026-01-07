package org.ccci.gto.android.common.androidx.recyclerview.decorator

import android.graphics.Rect
import android.view.View
import android.view.View.LAYOUT_DIRECTION_LTR
import android.view.View.LAYOUT_DIRECTION_RTL
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.converters.ConversionFailedException
import junitparams.converters.Converter
import junitparams.converters.Param
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(JUnitParamsRunner::class)
class MarginItemDecorationTest {
    private val all = Random.nextInt(0, 10)
    private val horizontal = Random.nextInt(10, 20)
    private val vertical = Random.nextInt(20, 30)
    private val left = Random.nextInt(30, 40)
    private val top = Random.nextInt(40, 50)
    private val right = Random.nextInt(50, 60)
    private val bottom = Random.nextInt(60, 70)
    private val start = Random.nextInt(70, 80)
    private val end = Random.nextInt(80, 90)

    @Test
    @Parameters("ltr", "rtl")
    fun `MarginItemDecoration()`(@Param(converter = LayoutDirectionConverter::class) dir: Int) {
        MarginItemDecoration().testDecoration(dir) {
            assertEquals(0, it.left)
            assertEquals(0, it.top)
            assertEquals(0, it.right)
            assertEquals(0, it.bottom)
        }
    }

    @Test
    @Parameters("ltr", "rtl")
    fun `MarginItemDecoration(margins=all)`(@Param(converter = LayoutDirectionConverter::class) dir: Int) {
        MarginItemDecoration(margins = all).testDecoration(dir) {
            assertEquals(all, it.left)
            assertEquals(all, it.top)
            assertEquals(all, it.right)
            assertEquals(all, it.bottom)
        }
    }

    @Test
    @Parameters("ltr", "rtl")
    fun `MarginItemDecoration(margins=all) - overrides`(@Param(converter = LayoutDirectionConverter::class) dir: Int) {
        MarginItemDecoration(margins = all, horizontalMargins = horizontal).testDecoration(dir) {
            assertEquals(horizontal, it.left)
            assertEquals(all, it.top)
            assertEquals(horizontal, it.right)
            assertEquals(all, it.bottom)
        }

        MarginItemDecoration(margins = all, horizontalMargins = horizontal, rightMargin = right).testDecoration(dir) {
            assertEquals(horizontal, it.left)
            assertEquals(all, it.top)
            assertEquals(right, it.right)
            assertEquals(all, it.bottom)
        }

        MarginItemDecoration(margins = all, verticalMargins = vertical).testDecoration(dir) {
            assertEquals(all, it.left)
            assertEquals(vertical, it.top)
            assertEquals(all, it.right)
            assertEquals(vertical, it.bottom)
        }

        MarginItemDecoration(margins = all, verticalMargins = vertical, bottomMargin = bottom).testDecoration(dir) {
            assertEquals(all, it.left)
            assertEquals(vertical, it.top)
            assertEquals(all, it.right)
            assertEquals(bottom, it.bottom)
        }
    }

    @Test
    @Parameters("ltr", "rtl")
    fun `MarginItemDecoration() - absolute`(@Param(converter = LayoutDirectionConverter::class) dir: Int) {
        MarginItemDecoration(
            leftMargin = left,
            topMargin = top,
            rightMargin = right,
            bottomMargin = bottom
        ).testDecoration(dir) {
            assertEquals(left, it.left)
            assertEquals(top, it.top)
            assertEquals(right, it.right)
            assertEquals(bottom, it.bottom)
        }
    }

    @Test
    @Parameters("ltr", "rtl")
    fun `MarginItemDecoration() - relative`(@Param(converter = LayoutDirectionConverter::class) dir: Int) {
        MarginItemDecoration(
            leftMargin = left,
            rightMargin = right,
            startMargin = start,
            endMargin = end
        ).testDecoration(dir) {
            assertNotEquals(left, it.left)
            assertNotEquals(right, it.right)
            when (dir) {
                LAYOUT_DIRECTION_LTR -> {
                    assertEquals(start, it.left)
                    assertEquals(end, it.right)
                }

                LAYOUT_DIRECTION_RTL -> {
                    assertEquals(end, it.left)
                    assertEquals(start, it.right)
                }

                else -> fail()
            }
        }
    }

    @Test
    fun `MarginItemDecoration() - relative - startMargin fallbacks`() {
        val decoration = MarginItemDecoration(
            leftMargin = left,
            rightMargin = right,
            startMargin = start
        )

        decoration.testDecoration(LAYOUT_DIRECTION_LTR) {
            assertEquals(start, it.left)
            assertNotEquals(left, it.left)
            assertEquals(right, it.right)
        }

        decoration.testDecoration(LAYOUT_DIRECTION_RTL) {
            assertEquals(left, it.left)
            assertEquals(start, it.right)
            assertNotEquals(right, it.right)
        }
    }

    @Test
    fun `MarginItemDecoration() - relative - endMargin fallbacks`() {
        val decoration = MarginItemDecoration(
            leftMargin = left,
            rightMargin = right,
            endMargin = end
        )

        decoration.testDecoration(LAYOUT_DIRECTION_LTR) {
            assertEquals(left, it.left)
            assertEquals(end, it.right)
            assertNotEquals(right, it.right)
        }

        decoration.testDecoration(LAYOUT_DIRECTION_RTL) {
            assertEquals(end, it.left)
            assertNotEquals(left, it.left)
            assertEquals(right, it.right)
        }
    }

    private fun MarginItemDecoration.testDecoration(dir: Int, block: (Rect) -> Unit) {
        val outRect = Rect()
        val view = mock<View> { on { layoutDirection } doReturn dir }
        getItemOffsets(outRect, view, mock(), mock())
        block(outRect)
    }

    class LayoutDirectionConverter : Converter<Param, Int> {
        override fun initialize(annotation: Param?) = Unit
        override fun convert(param: Any) = when (param) {
            "ltr" -> LAYOUT_DIRECTION_LTR
            "rtl" -> LAYOUT_DIRECTION_RTL
            else -> throw ConversionFailedException("Invalid layout direction: $param")
        }
    }
}
