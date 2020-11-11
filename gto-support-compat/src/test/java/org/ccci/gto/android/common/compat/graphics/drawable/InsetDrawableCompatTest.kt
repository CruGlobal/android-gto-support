package org.ccci.gto.android.common.compat.graphics.drawable

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21, 22])
class InsetDrawableCompatTest {
    @Test
    fun verifyGetIntrinsicWidthAndHeight() {
        val drawable = mock<Drawable> {
            on { intrinsicHeight } doReturn 1
            on { intrinsicWidth } doReturn 2
            on { getPadding(any()) } doAnswer {
                it.getArgument<Rect>(0).setEmpty()
                false
            }
        }
        val insetDrawable = InsetDrawableCompat(drawable, 4, 8, 16, 32)

        assertEquals(1 + 8 + 32, insetDrawable.intrinsicHeight)
        assertEquals(2 + 4 + 16, insetDrawable.intrinsicWidth)
    }
}
