package org.ccci.gto.android.common.lottie.databinding

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import org.ccci.gto.android.common.lottie.R
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class LottieAnimationViewBindingAdapterTest {
    private lateinit var view: LottieAnimationView

    @Before
    fun setup() {
        view = mock()
    }

    // region bindLoop()
    @Test
    fun testBindLoopEnabled() {
        view.bindLoop(true)
        verify(view).repeatCount = LottieDrawable.INFINITE
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testBindLoopDisabled() {
        view.bindLoop(false)
        verify(view).repeatCount = 0
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testBindLoopUndefined() {
        view.bindLoop(null)
        verify(view).repeatCount = 0
        verifyNoMoreInteractions(view)
    }
    // endregion bindLoop()

    // region bindAutoPlay()
    @Test
    fun testBindAutoPlayInitial() {
        view.bindAutoPlay(false)
        verify(view, never()).resumeAnimation()
        verify(view).pauseAnimation()
        verify(view).setTag(R.id.lottie_autoplay_enabled, false)
        clearInvocations(view)

        view.bindAutoPlay(true)
        verify(view).resumeAnimation()
        verify(view, never()).pauseAnimation()
        verify(view).setTag(R.id.lottie_autoplay_enabled, true)
    }

    @Test
    fun testBindAutoPlayCurrentlyDisabled() {
        view.stub { on { getTag(R.id.lottie_autoplay_enabled) } doReturn false }

        view.bindAutoPlay(false)
        verify(view).getTag(R.id.lottie_autoplay_enabled)
        verifyNoMoreInteractions(view)

        view.bindAutoPlay(true)
        verify(view).resumeAnimation()
        verify(view, never()).pauseAnimation()
        verify(view).setTag(R.id.lottie_autoplay_enabled, true)
    }

    @Test
    fun testBindAutoPlayCurrentlyEnabled() {
        view.stub { on { getTag(R.id.lottie_autoplay_enabled) } doReturn true }

        view.bindAutoPlay(true)
        verify(view).getTag(R.id.lottie_autoplay_enabled)
        verifyNoMoreInteractions(view)

        view.bindAutoPlay(false)
        verify(view, never()).resumeAnimation()
        verify(view).pauseAnimation()
        verify(view).setTag(R.id.lottie_autoplay_enabled, false)
    }
    // endregion bindAutoPlay()
}
