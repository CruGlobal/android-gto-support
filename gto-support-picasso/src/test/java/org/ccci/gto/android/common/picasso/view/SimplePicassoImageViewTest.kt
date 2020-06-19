package org.ccci.gto.android.common.picasso.view

import android.app.Activity
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class SimplePicassoImageViewTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get()
    }

    @Test
    fun verifyConstructionImageScaleTypeAttr() {
        val attrs = Robolectric.buildAttributeSet().addAttribute(android.R.attr.scaleType, "center").build()
        SimplePicassoImageView(context, attrs)
    }
}
