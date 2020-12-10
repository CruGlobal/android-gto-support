package org.ccci.gto.android.common.lottie.databinding

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test

class LottieAnimationViewBindingAdapterTest {
    private lateinit var view: LottieAnimationView

    @Before
    fun setup() {
        view = mock()
    }

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
}
