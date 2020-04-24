package org.ccci.gto.android.common.picasso.material

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.chip.Chip
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.picasso.Picasso
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEEP_STUBS

@RunWith(AndroidJUnit4::class)
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
    fun verifyTargetOnPrepareLoadSetsDrawable() {
        target.onPrepareLoad(drawable)
        verify(chip).chipIcon = drawable
    }

    @Test
    fun verifyTargetOnPrepareLoadClearsDrawableWhenDrawableIsNull() {
        target.onPrepareLoad(null)
        verify(chip).chipIcon = null
    }

    @Test
    fun verifyTargetOnBitmapLoadedCreatesAndSetsBitmapDrawable() {
        val resources: Resources = mock(defaultAnswer = RETURNS_DEEP_STUBS)
        val bitmap: Bitmap = mock()
        whenever(chip.resources).thenReturn(resources)

        target.onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
        argumentCaptor<BitmapDrawable> {
            verify(chip).chipIcon = capture()
            assertSame(bitmap, firstValue.bitmap)
        }
    }
}
