package org.ccci.gto.android.common.picasso.view

import android.content.Context
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.ccci.gto.android.common.picasso.R
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class SimplePicassoImageViewTest {
    private val context get() = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `testConstructor - attribute - placeholder resource`() {
        val attrs = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.placeholder, "@android:drawable/btn_default")
            .build()
        val view = TestSimplePicassoImageView(context, attrs)
        assertEquals(android.R.drawable.btn_default, view.helper.placeholderResId)
        assertNull(view.helper.placeholder)
    }

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

    class TestSimplePicassoImageView(context: Context, attrs: AttributeSet?) : SimplePicassoImageView(context, attrs) {
        public override val helper get() = super.helper
    }
}
