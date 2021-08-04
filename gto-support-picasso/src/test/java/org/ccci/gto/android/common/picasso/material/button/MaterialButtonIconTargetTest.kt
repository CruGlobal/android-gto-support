package org.ccci.gto.android.common.picasso.material.button

import android.graphics.drawable.Drawable
import com.google.android.material.button.MaterialButton
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class MaterialButtonIconTargetTest {
    private lateinit var button: MaterialButton
    private lateinit var target: MaterialButtonIconTarget
    private lateinit var drawable: Drawable

    @Before
    fun setup() {
        button = mock()
        target = MaterialButtonIconTarget.of(button)
        drawable = mock()
        reset(button)
    }

    @Test
    fun verifyTargetIsSaved() {
        val target = MaterialButtonIconTarget.of(button)
        verify(button).setTag(any(), eq(target))
    }

    @Test
    fun verifyTargetIsReused() {
        whenever(button.getTag(any())).thenReturn(target)

        assertSame(target, MaterialButtonIconTarget.of(button))
        verify(button).getTag(any())
    }

    @Test
    fun verifyUpdateDrawableSetsDrawable() {
        target.updateDrawable(drawable)
        verify(button).icon = drawable
    }

    @Test
    fun verifyUpdateDrawableClearsDrawableWhenDrawableIsNull() {
        target.updateDrawable(null)
        verify(button).icon = null
    }
}
