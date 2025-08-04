package org.ccci.gto.android.common.picasso.material

import android.graphics.drawable.Drawable
import com.google.android.material.chip.Chip
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChipIconTargetTest {
    private lateinit var chip: Chip
    private lateinit var target: ChipIconTarget
    private lateinit var drawable: Drawable

    @BeforeTest
    fun setup() {
        chip = mock()
        target = ChipIconTarget.of(chip)
        drawable = mock()
        reset(chip)
    }

    @Test
    fun verifyTargetIsSaved() {
        val target = ChipIconTarget.of(chip)
        verify(chip).setTag(any(), eq(target))
    }

    @Test
    fun verifyTargetIsReused() {
        whenever(chip.getTag(any())).thenReturn(target)

        assertSame(target, ChipIconTarget.of(chip))
        verify(chip).getTag(any())
    }

    @Test
    fun verifyUpdateDrawableSetsDrawable() {
        target.updateDrawable(drawable)
        verify(chip).chipIcon = drawable
    }

    @Test
    fun verifyUpdateDrawableClearsDrawableWhenDrawableIsNull() {
        target.updateDrawable(null)
        verify(chip).chipIcon = null
    }
}
