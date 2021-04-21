package org.ccci.gto.android.common.picasso.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class SimplePicassoImageViewTest {
    private val context get() = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `testConstructor - scaleType Attribute`() {
        val attrs = Robolectric.buildAttributeSet().addAttribute(android.R.attr.scaleType, "center").build()
        SimplePicassoImageView(context, attrs)
    }

    @Test
    fun `testConstructor - scaleType Attribute - Subclass`() {
        val attrs = Robolectric.buildAttributeSet().addAttribute(android.R.attr.scaleType, "center").build()
        object : SimplePicassoImageView(context, attrs) {
            override val helper by lazy { PicassoImageView.Helper(this, attrs) }
        }
    }
}
