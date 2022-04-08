package org.ccci.gto.android.common.lottie.databinding

import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.setCompositionTask
import java.io.File
import org.ccci.gto.android.common.lottie.R
import org.ccci.gto.android.common.lottie.clearComposition
import org.ccci.gto.android.common.lottie.loadLottieComposition

private const val ANIMATION = "animation"

@BindingAdapter("lottie_loop")
internal fun LottieAnimationView.bindLoop(loop: Boolean?) {
    repeatCount = if (loop == true) LottieDrawable.INFINITE else 0
}

@BindingAdapter("lottie_autoPlay")
internal fun LottieAnimationView.bindAutoPlay(autoPlay: Boolean) {
    // short-circuit if autoPlay setting isn't changing
    if (getTag(R.id.lottie_autoplay_enabled) == autoPlay) return

    if (autoPlay) {
        resumeAnimation()
    } else {
        pauseAnimation()
    }
    setTag(R.id.lottie_autoplay_enabled, autoPlay)
}

@BindingAdapter(ANIMATION)
internal fun LottieAnimationView.bindAnimationFile(file: File?) = when {
    file != null -> setCompositionTask(file.loadLottieComposition())
    else -> clearComposition()
}
