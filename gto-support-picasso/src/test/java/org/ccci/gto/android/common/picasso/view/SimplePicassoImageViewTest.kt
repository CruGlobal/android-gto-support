package org.ccci.gto.android.common.picasso.view

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class SimplePicassoImageViewTest {
    @Test
    fun verifyConstructionImageScaleTypeAttr() {
        val attrs = Robolectric.buildAttributeSet().addAttribute(android.R.attr.scaleType, "center").build()
        SimplePicassoImageView(ApplicationProvider.getApplicationContext(), attrs)
    }
}
