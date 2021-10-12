package org.ccci.gto.android.common.picasso

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.squareup.picasso.Picasso
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEEP_STUBS

@RunWith(AndroidJUnit4::class)
class BaseViewTargetTest {
    private lateinit var drawable: Drawable
    private lateinit var view: ImageView
    private lateinit var target: BaseViewTarget<ImageView>

    @Before
    fun setup() {
        drawable = mock()
        view = mock(defaultAnswer = RETURNS_DEEP_STUBS)
        target = object : BaseViewTarget<ImageView>(view) {
            override fun updateDrawable(drawable: Drawable?) = view.setImageDrawable(drawable)
        }
    }

    @Test
    fun verifyTargetOnPrepareLoadSetsDrawable() {
        target.onPrepareLoad(drawable)
        verify(view).setImageDrawable(drawable)
    }

    @Test
    fun verifyTargetOnPrepareLoadClearsDrawableWhenDrawableIsNull() {
        target.onPrepareLoad(null)
        verify(view).setImageDrawable(null)
    }

    @Test
    fun verifyTargetOnBitmapLoadedCreatesAndSetsBitmapDrawable() {
        val bitmap = mock<Bitmap>()

        target.onBitmapLoaded(bitmap, Picasso.LoadedFrom.DISK)
        argumentCaptor<BitmapDrawable> {
            verify(view).setImageDrawable(capture())
            Assert.assertSame(bitmap, firstValue.bitmap)
        }
    }
}
