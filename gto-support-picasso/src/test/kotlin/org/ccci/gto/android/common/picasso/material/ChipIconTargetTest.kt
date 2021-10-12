package org.ccci.gto.android.common.picasso.material

import android.graphics.drawable.Drawable
import com.google.android.material.chip.Chip
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class ChipIconTargetTest {
    private lateinit var chip: Chip
    private lateinit var target: ChipIconTarget
    private lateinit var drawable: Drawable

    @Before
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
