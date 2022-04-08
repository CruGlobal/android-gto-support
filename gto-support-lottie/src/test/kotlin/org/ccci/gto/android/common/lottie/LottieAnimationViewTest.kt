package org.ccci.gto.android.common.lottie

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.setCompositionTask
import com.airbnb.lottie.wrapInLottieTask
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LottieAnimationViewTest {
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
