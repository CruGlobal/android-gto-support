package com.airbnb.lottie

import org.ccci.gto.android.common.util.getDeclaredMethodOrNull

private val setCompositionTaskMethod by lazy {
    getDeclaredMethodOrNull<LottieAnimationView>("setCompositionTask", LottieTask::class.java)
}
internal val clearCompositionMethod by lazy {
    getDeclaredMethodOrNull<LottieAnimationView>("clearComposition")
}

internal fun LottieAnimationView.setCompositionTask(task: LottieTask<LottieComposition>) {
    setCompositionTaskMethod?.invoke(this, task)
}

internal fun LottieAnimationView.clearComposition() {
    clearCompositionMethod?.invoke(this)
}
