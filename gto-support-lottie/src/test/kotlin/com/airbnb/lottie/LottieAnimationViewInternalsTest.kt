package com.airbnb.lottie

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.ccci.gto.android.common.lottie.TEST_ANIM_1
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LottieAnimationViewInternalsTest {
    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun testClearComposition() {
        val view = LottieAnimationView(context)
        assertNull(view.composition)
        view.setCompositionTask(
            LottieCompositionFactory.fromJsonStringSync(TEST_ANIM_1, null).wrapInLottieTask()
        )
        assertNotNull(view.composition)
        view.clearComposition()
        assertNull(view.composition)
    }
}
