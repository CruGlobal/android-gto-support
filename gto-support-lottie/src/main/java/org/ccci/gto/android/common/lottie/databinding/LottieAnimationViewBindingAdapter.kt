package org.ccci.gto.android.common.lottie.databinding

import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import org.ccci.gto.android.common.lottie.R

@BindingAdapter("lottie_loop")
internal fun LottieAnimationView.bindLoop(loop: Boolean?) {
    repeatCount = if (loop == true) LottieDrawable.INFINITE else 0
}

@BindingAdapter("lottie_autoPlay")
internal fun LottieAnimationView.bindAutoPlay(autoPlay: Boolean) {
    // short-circuit if autoPlay setting isn't changing
    if (getTag(R.id.lottie_autoplay_enabled) == autoPlay) return

    if (autoPlay) {
        playAnimation()
    } else {
        pauseAnimation()
    }
    setTag(R.id.lottie_autoplay_enabled, autoPlay)
}
