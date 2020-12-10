package org.ccci.gto.android.common.lottie.databinding

import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

@BindingAdapter("lottie_loop")
internal fun LottieAnimationView.bindLoop(loop: Boolean?) {
    repeatCount = if (loop == true) LottieDrawable.INFINITE else 0
}
